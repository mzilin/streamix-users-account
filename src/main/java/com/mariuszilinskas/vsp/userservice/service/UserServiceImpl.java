package com.mariuszilinskas.vsp.userservice.service;

import com.mariuszilinskas.vsp.userservice.client.AuthFeignClient;
import com.mariuszilinskas.vsp.userservice.dto.*;
import com.mariuszilinskas.vsp.userservice.enums.UserRole;
import com.mariuszilinskas.vsp.userservice.enums.UserStatus;
import com.mariuszilinskas.vsp.userservice.exception.EmailExistsException;
import com.mariuszilinskas.vsp.userservice.exception.PasswordValidationException;
import com.mariuszilinskas.vsp.userservice.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.userservice.exception.UserRegistrationException;
import com.mariuszilinskas.vsp.userservice.producer.RabbitMQProducer;
import com.mariuszilinskas.vsp.userservice.model.User;
import com.mariuszilinskas.vsp.userservice.repository.UserRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for managing user accounts.
 * This service handles user creation, information updates, and deletion.
 *
 * @author Marius Zilinskas
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final AuthFeignClient authFeignClient;
    private final UserRepository userRepository;
    private final RabbitMQProducer rabbitMQProducer;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request){
        logger.info("Creating new User with Email: '{}'", request.email());

        checkEmailExists(request.email());
        User newUser = populateNewUserWithRequestData(request);
        var credentialsRequest = new CredentialsRequest(newUser.getId(), request.password());
        createUserCredentials(credentialsRequest);

        var rabbitRequest = new CreateUserDefaultProfileRequest(newUser.getId(), newUser.getFirstName());
        rabbitMQProducer.sendCreateUserDefaultProfileMessage(rabbitRequest);

        return mapToUserResponse(newUser);
    }

    private User populateNewUserWithRequestData(CreateUserRequest request) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setCountry(request.country());
        user.setRoles(List.of(UserRole.USER));
        user.setStatus(UserStatus.PENDING);
        return userRepository.save(user);
    }

    private void createUserCredentials(CredentialsRequest request) {
        try {
            authFeignClient.createPasswordAndSetPasscode(request);
        } catch (FeignException ex) {
            UUID userId = request.userId();
            logger.error("Feign Exception when creating Password and Passcode for User [id: {}]: Status {}, Body {}",
                    userId, ex.status(), ex.contentUTF8());
            userRepository.deleteById(userId);  // Rollback user creation
            throw new UserRegistrationException(userId);
        }
    }

    @Override
    public UserResponse getUser(UUID userId) {
        logger.info("Getting User [id: '{}'", userId);
        User user = findUserById(userId);
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        logger.info("Updating User [id: '{}']", userId);
        User user = findUserById(userId);
        applyUserUpdates(user, request);
        return mapToUserResponse(user);
    }

    private void applyUserUpdates(User user, UpdateUserRequest request) {
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setCountry(request.country());
        userRepository.save(user);
    }

    private static UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCountry(),
                user.getStatus().name()
        );
    }

    @Override
    public UpdateEmailResponse updateUserEmail(UUID userId, UpdateEmailRequest request) {
        logger.info("Updating User Email [id: '{}'", userId);

        User user = findUserById(userId);
        var passwordRequest = new VerifyPasswordRequest(userId, request.password());

        verifyPassword(passwordRequest);
        checkEmailExists(request.email());
        applyEmailUpdate(user, request);

        rabbitMQProducer.sendResetPasscodeMessage(userId);

        return new UpdateEmailResponse(userId, user.getEmail(), user.isEmailVerified());
    }

    private void checkEmailExists(String email) {
        if (userRepository.existsByEmail(email))
            throw new EmailExistsException();
    }

    private void applyEmailUpdate(User user, UpdateEmailRequest request) {
        user.setEmail(request.email());
        user.setEmailVerified(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void verifyUser(UUID userId) {
        logger.info("Verifying User [id: '{}'", userId);
        User user = findUserById(userId);
        markEmailAsVerified(user);
    }

    private void markEmailAsVerified(User user) {
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    @Override
    public AuthDetailsResponse getUserAuthDetailsByEmail(String email) {
        logger.info("Getting Auth Details for User [email: '{}'", email);
        User user = findUserByEmail(email);
        return updateLastActiveAndMapToAuthResponse(user);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, "email", email));
    }

    @Override
    public AuthDetailsResponse getUserAuthDetailsByUserId(UUID userId) {
        logger.info("Getting Auth Details for User [id: '{}'", userId);
        User user = findUserById(userId);
        return updateLastActiveAndMapToAuthResponse(user);
    }

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, "id", userId));
    }

    private AuthDetailsResponse updateLastActiveAndMapToAuthResponse(User user) {
        user.setLastActive(ZonedDateTime.now());
        userRepository.save(user);
        return mapUserToAuthResponse(user);
    }

    private AuthDetailsResponse mapUserToAuthResponse(User user) {
        return new AuthDetailsResponse(
                user.getId(),
                user.getRoles(),
                user.getAuthorities(),
                user.getStatus()
        );
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId, DeleteUserRequest request) {
        logger.info("Deleting User [userId: '{}'], and its data", userId);
        var passwordRequest = new VerifyPasswordRequest(userId, request.password());
        verifyPassword(passwordRequest);
        userRepository.deleteById(userId);
        rabbitMQProducer.sendDeleteUserDataMessage(userId);
    }

    private void verifyPassword(VerifyPasswordRequest request) {
        try {
            authFeignClient.verifyPassword(request);
        } catch (FeignException ex) {
            logger.error("Feign Exception when verifying password: Status {}, Body {}", ex.status(), ex.contentUTF8());
            throw new PasswordValidationException();
        }
    }

}

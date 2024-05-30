package com.mariuszilinskas.vsp.userservice.service;

import com.mariuszilinskas.vsp.userservice.client.AuthFeignClient;
import com.mariuszilinskas.vsp.userservice.dto.*;
import com.mariuszilinskas.vsp.userservice.enums.UserRole;
import com.mariuszilinskas.vsp.userservice.enums.UserStatus;
import com.mariuszilinskas.vsp.userservice.exception.EmailExistsException;
import com.mariuszilinskas.vsp.userservice.exception.PasswordValidationException;
import com.mariuszilinskas.vsp.userservice.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.userservice.exception.UserRegistrationException;
import com.mariuszilinskas.vsp.userservice.model.User;
import com.mariuszilinskas.vsp.userservice.repository.UserRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request){
        logger.info("Creating new User with Email: '{}'", request.email());

        checkEmailExists(request.email());
        User newUser = populateNewUserWithRequestData(request);
        User createdUser = userRepository.save(newUser);
        createUserCredentials(createdUser.getId(), request.password());

        return toUserResponse(createdUser);
    }

    private User populateNewUserWithRequestData(CreateUserRequest request) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setCountry(request.country());
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.PENDING);

        // TODO: create default profiles & avatars + UPDATE TESTS

        return user;
    }

    private void createUserCredentials(UUID userId, String password) {
        try {
            CredentialsRequest request = new CredentialsRequest(userId, password);
            authFeignClient.createPasswordAndSetPasscode(request);
        } catch (FeignException ex) {
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
        return toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        logger.info("Updating User [id: '{}']", userId);
        User user = findUserById(userId);
        applyUserUpdates(user, request);
        return toUserResponse(user);
    }

    private void applyUserUpdates(User user, UpdateUserRequest request) {
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        userRepository.save(user);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCountry(),
                user.isEmailVerified(),
                user.getStatus().name(),
                user.getRole().name(),
                user.getUserProfiles(),
                user.getCreatedAt(),
                user.getLastActive()
        );
    }

    @Override
    public UpdateEmailResponse updateUserEmail(UUID userId, UpdateEmailRequest request) {
        logger.info("Updating User Email [id: '{}'", userId);

        User user = findUserById(userId);
        verifyPassword(userId, request.password());
        checkEmailExists(request.email());
        applyEmailUpdate(user, request);

        // TODO: RabbitMQ - send request to create passcode and send verification email

        return new UpdateEmailResponse(userId, user.getEmail(), user.isEmailVerified());
    }

    private void verifyPassword(UUID userId, String password) {
        try {
            CredentialsRequest request = new CredentialsRequest(userId, password);
            authFeignClient.verifyPassword(request);
        } catch (FeignException ex) {
            logger.error("Feign Exception when verifying password: Status {}, Body {}", ex.status(), ex.contentUTF8());
            throw new PasswordValidationException();
        }
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
    public UUID getUserIdByEmail(String email) {
        logger.info("Getting User ID [email: '{}'", email);
        User user = findUserByEmail(email);
        return user.getId();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, "email", email));
    }

    @Override
    public UserRole getUserRole(UUID userId) {
        logger.info("Getting User Role for User [userId: '{}'", userId);
        User user = findUserById(userId);
        return user.getRole();
    }

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, "id", userId));
    }

    @Override
    public void deleteUser(UUID userId, DeleteUserRequest request) {
        // TODO: RabbitMQ to all services for user data deletion
    }
}

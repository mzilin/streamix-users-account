package com.mariuszilinskas.vsp.users.account.service;

import com.mariuszilinskas.vsp.users.account.client.IdentityFeignClient;
import com.mariuszilinskas.vsp.users.account.dto.*;
import com.mariuszilinskas.vsp.users.account.exception.EmailExistsException;
import com.mariuszilinskas.vsp.users.account.exception.PasswordValidationException;
import com.mariuszilinskas.vsp.users.account.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.users.account.exception.CreateCredentialsException;
import com.mariuszilinskas.vsp.users.account.mapper.UserMapper;
import com.mariuszilinskas.vsp.users.account.producer.RabbitMQProducer;
import com.mariuszilinskas.vsp.users.account.model.User;
import com.mariuszilinskas.vsp.users.account.repository.UserRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
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
    private final IdentityFeignClient identityFeignClient;
    private final UserRepository userRepository;
    private final RabbitMQProducer rabbitMQProducer;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request){
        logger.info("Creating new User with Email: '{}']", request.email());

        checkEmailExists(request.email());
        User newUser = createAndSaveUser(request);

        var credentialsRequest = UserMapper.mapToCredentialsRequest(newUser, request.password());
        createCredentials(credentialsRequest);  // TODO: use gRPC

        var profileRequest = UserMapper.mapToDefaultProfileMessage(newUser);
        rabbitMQProducer.sendCreateDefaultProfileMessage(profileRequest);

        return UserMapper.mapToUserResponse(newUser);
    }

    private User createAndSaveUser(CreateUserRequest request) {
        User user = UserMapper.mapFromCreateRequest(request);
        return userRepository.save(user);
    }

    private void createCredentials (CredentialsRequest request) {
        try {
            identityFeignClient.createCredentials(request);
        } catch (FeignException ex) {
            logger.error("Feign Exception when creating user credentials: Status {}, Body {}", ex.status(), ex.contentUTF8());
            throw new CreateCredentialsException(request.userId());
        }
    }

    @Override
    public UserResponse getUser(UUID userId) {
        logger.info("Getting User [id: '{}']", userId);
        User user = findUserById(userId);
        return UserMapper.mapToUserResponse(user);
    }

    @Override
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        logger.info("Updating User [id: '{}']", userId);
        User user = findUserById(userId);
        updateAndSaveUser(user, request);
        return UserMapper.mapToUserResponse(user);
    }

    private void updateAndSaveUser(User user, UpdateUserRequest request) {
        UserMapper.applyUpdates(user, request);
        userRepository.save(user);
    }

    @Override
    public UpdateEmailResponse updateUserEmail(UUID userId, UpdateEmailRequest request) {
        logger.info("Updating User Email [id: '{}']", userId);

        User user = findUserById(userId);
        var passwordRequest = new VerifyPasswordRequest(userId, request.password());

        verifyPassword(passwordRequest);
        checkEmailExists(request.email());
        updateEmail(user, request);

        rabbitMQProducer.sendResetPasscodeMessage(userId);

        return UserMapper.mapToUpdateEmailResponse(user);
    }

    private void checkEmailExists(String email) {
        if (userRepository.existsByEmail(email))
            throw new EmailExistsException();
    }

    private void updateEmail(User user, UpdateEmailRequest request) {
        user.setEmail(request.email());
        user.setEmailVerified(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void verifyUser(UUID userId) {
        logger.info("Verifying User [id: '{}']", userId);
        User user = findUserById(userId);
        markEmailAsVerified(user);
    }

    private void markEmailAsVerified(User user) {
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    @Override
    public AuthDetailsResponse getUserAuthDetailsByEmail(String email) {
        logger.info("Getting Auth Details for User [email: '{}']", email);
        User user = findUserByEmail(email);
        updateLastActive(user.getId());
        return UserMapper.mapToAuthDetailsResponse(user);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, "email", email));
    }

    @Override
    public AuthDetailsResponse getUserAuthDetailsByUserId(UUID userId) {
        logger.info("Getting Auth Details for User [id: '{}']", userId);
        User user = findUserById(userId);
        updateLastActive(userId);
        return UserMapper.mapToAuthDetailsResponse(user);
    }

    private void updateLastActive(UUID userId) {
        var message = new UserLastActiveMessage(userId, ZonedDateTime.now());
        rabbitMQProducer.sendUpdateLastActiveMessage(message);
    }

    @Override
    @Transactional
    public void updateLastActiveInDb(UUID userId, ZonedDateTime lastActive) {
        logger.info("Updating lastActive for User [userId: '{}']", userId);
        User user = findUserById(userId);
        user.setLastActive(lastActive);
        userRepository.save(user);
    }

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, "id", userId));
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
            identityFeignClient.verifyPassword(request);
        } catch (FeignException ex) {
            logger.error("Feign Exception when verifying password: Status {}, Body {}", ex.status(), ex.contentUTF8());
            throw new PasswordValidationException();
        }
    }

}

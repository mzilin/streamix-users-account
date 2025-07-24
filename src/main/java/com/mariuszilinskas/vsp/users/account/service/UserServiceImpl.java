package com.mariuszilinskas.vsp.users.account.service;

import com.mariuszilinskas.vsp.users.account.client.AuthFeignClient;
import com.mariuszilinskas.vsp.users.account.dto.*;
import com.mariuszilinskas.vsp.users.account.exception.EmailExistsException;
import com.mariuszilinskas.vsp.users.account.exception.PasswordValidationException;
import com.mariuszilinskas.vsp.users.account.exception.ResourceNotFoundException;
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
    private final AuthFeignClient authFeignClient;
    private final UserRepository userRepository;
    private final RabbitMQProducer rabbitMQProducer;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request){
        logger.info("Creating new User with Email: '{}']", request.email());

        checkEmailExists(request.email());
        User newUser = createAndSaveUser(request);

        var credentialsRequest = UserMapper.toCredentialsRequest(newUser, request.password());
        rabbitMQProducer.sendCreateCredentialsMessage(credentialsRequest);

        var profileRequest = UserMapper.toDefaultProfileRequest(newUser);
        rabbitMQProducer.sendCreateUserDefaultProfileMessage(profileRequest);

        return UserMapper.mapToUserResponse(newUser);
    }

    private User createAndSaveUser(CreateUserRequest request) {
        User user = UserMapper.mapFromCreateRequest(request);
        userRepository.save(user);  // TODO: send via KAFKA to update
        return user;
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
        userRepository.save(user);  // TODO: send via KAFKA to update
    }

    @Override
    public UpdateEmailResponse updateUserEmail(UUID userId, UpdateEmailRequest request) {
        logger.info("Updating User Email [id: '{}']", userId);

        User user = findUserById(userId);
        var passwordRequest = new VerifyPasswordRequest(userId, request.password());

        verifyPassword(passwordRequest);
        checkEmailExists(request.email());
        applyEmailUpdate(user, request);

        rabbitMQProducer.sendResetPasscodeMessage(userId);

        return UserMapper.mapToUpdateEmailResponse(user);
    }

    private void checkEmailExists(String email) {
        if (userRepository.existsByEmail(email))
            throw new EmailExistsException();
    }

    private void applyEmailUpdate(User user, UpdateEmailRequest request) {
        user.setEmail(request.email());
        user.setEmailVerified(false);
        userRepository.save(user);  // TODO: send via KAFKA to update
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
        userRepository.save(user);  // TODO: send via KAFKA to update
    }

    @Override
    public AuthDetailsResponse getUserAuthDetailsByEmail(String email) {
        logger.info("Getting Auth Details for User [email: '{}']", email);
        User user = findUserByEmail(email);
        updateLastActive(user);
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
        updateLastActive(user);
        return UserMapper.mapToAuthDetailsResponse(user);
    }

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, "id", userId));
    }

    private void updateLastActive(User user) {
        user.setLastActive(ZonedDateTime.now());
        userRepository.save(user);  // TODO: send via KAFKA to update
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

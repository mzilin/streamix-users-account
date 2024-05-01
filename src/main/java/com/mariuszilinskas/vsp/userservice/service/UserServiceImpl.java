package com.mariuszilinskas.vsp.userservice.service;

import com.mariuszilinskas.vsp.userservice.client.AuthFeignClient;
import com.mariuszilinskas.vsp.userservice.dto.CreateCredentialsRequest;
import com.mariuszilinskas.vsp.userservice.dto.CreateUserRequest;
import com.mariuszilinskas.vsp.userservice.dto.UserResponse;
import com.mariuszilinskas.vsp.userservice.enums.UserRole;
import com.mariuszilinskas.vsp.userservice.enums.UserStatus;
import com.mariuszilinskas.vsp.userservice.exception.EmailExistsException;
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
        createUserCredentials(createdUser.getId(), request);

        return toUserResponse(createdUser);
    }

    private void checkEmailExists(String email) {
        if (userRepository.existsByEmail(email))
            throw new EmailExistsException();
    }

    private User populateNewUserWithRequestData(CreateUserRequest request) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.PENDING);
        return user;
    }

    private void createUserCredentials(UUID userId, CreateUserRequest request) {
        try {
            CreateCredentialsRequest credentialsRequest = new CreateCredentialsRequest(
                    userId,
                    request.email(),
                    request.password()
            );
            authFeignClient.createPasswordAndSetPasscode(credentialsRequest);
        } catch (FeignException ex) {
            logger.error("Failed to create password and passcode: {}", ex.getMessage());
            userRepository.deleteById(userId);  // Rollback user creation
            throw new UserRegistrationException(userId);
        }
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getStatus().name(),
                user.getRole().name(),
                user.getUserProfiles(),
                user.getCreatedAt(),
                user.getLastActive()
        );
    }

    @Override
    @Transactional
    public void updateUserStatus(UUID userId, UserStatus newStatus) {
        //
    }
}

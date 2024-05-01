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
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    private AuthFeignClient authFeignClient;

    @InjectMocks
    UserServiceImpl userService;

    private CreateUserRequest createUserRequest;
    private FeignException feignException;
    private final UUID userId = UUID.randomUUID();
    private final User user = new User();
    private final User user2 = new User();

    // ------------------------------------

    @BeforeEach
    void setUp() {
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");

        user2.setId(UUID.randomUUID());
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setEmail("jane@example.com");

        createUserRequest = new CreateUserRequest(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                "Password123!"
        );

        // Empty string for URL as a placeholder
        Request feignRequest = Request.create(
                Request.HttpMethod.POST,
                "", // Empty string for URL as a placeholder
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );

        feignException = new FeignException.NotFound(
                "Not found", feignRequest, null, Collections.emptyMap()
        );
    }

    // ------------------------------------

    @Test
    void testCreateUser_Success() {
        // Arrange
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        user.setStatus(UserStatus.PENDING);
        user.setRole(UserRole.USER);

        when(userRepository.existsByEmail(createUserRequest.email()))
                .thenReturn(false);
        when(userRepository.save(captor.capture())).thenReturn(user);
        when(authFeignClient.createPasswordAndSetPasscode(any(CreateCredentialsRequest.class))).thenReturn(null);

        // Act
        UserResponse response = userService.createUser(createUserRequest);

        // Assert
        assertNotNull(response);
        assertEquals(user.getId(), response.id());

        verify(userRepository, times(1)).existsByEmail(createUserRequest.email());
        verify(userRepository, times(1)).save(captor.capture());
        verify(authFeignClient, times(1)).createPasswordAndSetPasscode(any(CreateCredentialsRequest.class));

        User savedUser = captor.getValue();
        assertEquals(createUserRequest.firstName(), savedUser.getFirstName());
        assertEquals(createUserRequest.lastName(), savedUser.getLastName());
        assertEquals(createUserRequest.email(), savedUser.getEmail());
        assertFalse(savedUser.isEmailVerified());
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(createUserRequest.email())).thenReturn(true);

        //Act & Assert
        assertThrows(EmailExistsException.class, () -> userService.createUser(createUserRequest));

        // Assert
        verify(userRepository, times(1)).existsByEmail(createUserRequest.email());
        verify(userRepository, never()).save(any(User.class));
        verify(authFeignClient, never()).createPasswordAndSetPasscode(any(CreateCredentialsRequest.class));
    }

    @Test
    void testCreateUser_FailsToCreateUserCredentials() {
        // Arrange
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        user.setStatus(UserStatus.PENDING);
        user.setRole(UserRole.USER);

        when(userRepository.existsByEmail(createUserRequest.email()))
                .thenReturn(false);
        when(userRepository.save(captor.capture())).thenReturn(user);
        doThrow(feignException).when(authFeignClient).createPasswordAndSetPasscode(any(CreateCredentialsRequest.class));
        doNothing().when(userRepository).deleteById(userId);

        //Act & Assert
        assertThrows(UserRegistrationException.class, () -> userService.createUser(createUserRequest));

        // Assert
        verify(userRepository, times(1)).existsByEmail(createUserRequest.email());
        verify(userRepository, times(1)).save(captor.capture());
        verify(authFeignClient, times(1)).createPasswordAndSetPasscode(any(CreateCredentialsRequest.class));
        verify(userRepository, times(1)).deleteById(userId);
    }

}

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
import java.util.Optional;
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
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(UserRole.USER);

        user2.setId(UUID.randomUUID());
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setEmail("jane@example.com");
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(UserRole.USER);

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

        when(userRepository.existsByEmail(createUserRequest.email()))
                .thenReturn(false);
        when(userRepository.save(captor.capture())).thenReturn(user);
        when(authFeignClient.createPasswordAndSetPasscode(any(CredentialsRequest.class))).thenReturn(null);

        // Act
        UserResponse response = userService.createUser(createUserRequest);

        // Assert
        assertNotNull(response);
        assertEquals(user.getId(), response.id());

        verify(userRepository, times(1)).existsByEmail(createUserRequest.email());
        verify(userRepository, times(1)).save(captor.capture());
        verify(authFeignClient, times(1)).createPasswordAndSetPasscode(any(CredentialsRequest.class));

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
        verify(authFeignClient, never()).createPasswordAndSetPasscode(any(CredentialsRequest.class));
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
        doThrow(feignException).when(authFeignClient).createPasswordAndSetPasscode(any(CredentialsRequest.class));
        doNothing().when(userRepository).deleteById(userId);

        // Act & Assert
        assertThrows(UserRegistrationException.class, () -> userService.createUser(createUserRequest));

        // Assert
        verify(userRepository, times(1)).existsByEmail(createUserRequest.email());
        verify(userRepository, times(1)).save(captor.capture());
        verify(authFeignClient, times(1)).createPasswordAndSetPasscode(any(CredentialsRequest.class));
        verify(userRepository, times(1)).deleteById(userId);
    }

    // ------------------------------------

    @Test
    void testGetUser_Success() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserResponse response = userService.getUser(userId);

        // Assert
        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        assertEquals(user.getFirstName(), response.firstName());
        assertEquals(user.getLastName(), response.lastName());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getStatus().name(), response.status());
        assertEquals(user.getRole().name(), response.role());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUser_NonExistentUser() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Assert & Act
        assertThrows(ResourceNotFoundException.class, () -> userService.getUser(nonExistentId));

        verify(userRepository, times(1)).findById(nonExistentId);
    }

    // ------------------------------------

    @Test
    void testUpdateUser_Success() {
        // Arrange
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        UpdateUserRequest request = new UpdateUserRequest("UpdatedFirstName", "UpdatedLastName");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(captor.capture())).thenReturn(user);

        // Act
        UserResponse response = userService.updateUser(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals(user.getId(), response.id());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals(request.firstName(), savedUser.getFirstName());
        assertEquals(request.lastName(), savedUser.getLastName());
    }

    @Test
    void testUpdateUser_NonExistentUser() {
        // Arrange
        UpdateUserRequest request = new UpdateUserRequest("UpdatedFirstName", "UpdatedLastName");

        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Assert & Act
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(nonExistentId, request));

        // Assert
        verify(userRepository, times(1)).findById(nonExistentId);
        verify(userRepository, never()).save(any(User.class));
    }

    // ------------------------------------

    @Test
    void testUpdateUserEmail_Success() {
        // Arrange
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        String password = "Password1!";
        String currentEmail = "current_email@example.com";
        String newEmail = "new_email@example.com";

        user.setEmail(currentEmail);

        UpdateEmailRequest request = new UpdateEmailRequest(newEmail, password);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(authFeignClient.verifyPassword(any(CredentialsRequest.class))).thenReturn(null);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.save(captor.capture())).thenReturn(user);

        // Act
        UpdateEmailResponse response = userService.updateUserEmail(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals(user.getId(), response.userId());
        assertEquals(newEmail, response.email());
        assertFalse(response.isEmailVerified());

        verify(userRepository, times(1)).findById(userId);
        verify(authFeignClient, times(1)).verifyPassword(any(CredentialsRequest.class));
        verify(userRepository, times(1)).existsByEmail(request.email());
        verify(userRepository, times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals(newEmail, savedUser.getEmail());
    }

    @Test
    void testUpdateUserEmail_PasswordsDontMatch() {
        // Arrange
        String password = "wrongPassword";
        String currentEmail = "previous_email@example.com";
        String newEmail = "new_email@example.com";

        user.setEmail(currentEmail);

        UpdateEmailRequest request = new UpdateEmailRequest(newEmail, password);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doThrow(feignException).when(authFeignClient).verifyPassword(any(CredentialsRequest.class));

        // Act & Assert
        assertThrows(PasswordValidationException.class, () -> userService.updateUserEmail(userId, request));

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(authFeignClient, times(1)).verifyPassword(any(CredentialsRequest.class));
        verify(userRepository, never()).existsByEmail(request.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserEmail_NonExistentUser() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        UpdateEmailRequest request = new UpdateEmailRequest("updated_email@example.com", "currentPassword");

        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Assert & Act
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUserEmail(nonExistentId, request));

        // Assert
        verify(userRepository, times(1)).findById(nonExistentId);
        verify(authFeignClient, never()).verifyPassword(any(CredentialsRequest.class));
        verify(userRepository, never()).existsByEmail(request.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserEmail_NewEmailAlreadyInUse() {
        // Arrange
        UpdateEmailRequest request = new UpdateEmailRequest("email_in_use@example.com", "currentPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(authFeignClient.verifyPassword(any(CredentialsRequest.class))).thenReturn(null);
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // Assert & Act
        assertThrows(EmailExistsException.class, () -> userService.updateUserEmail(userId, request));

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(authFeignClient, times(1)).verifyPassword(any(CredentialsRequest.class));
        verify(userRepository, times(1)).existsByEmail(request.email());
        verify(userRepository, never()).save(any(User.class));
    }

    // ------------------------------------

    @Test
    void testVerifyUserEmail_Success() {
        // Arrange
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(captor.capture())).thenReturn(user);

        // Act
        userService.verifyUser(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        assertTrue(savedUser.isEmailVerified());
    }

    @Test
    void testVerifyUserEmail_NonExistentUser() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Assert & Act
        assertThrows(ResourceNotFoundException.class, () -> userService.verifyUser(nonExistentId));

        // Assert
        verify(userRepository, times(1)).findById(nonExistentId);
        verify(userRepository, never()).save(any(User.class));
    }

    // ------------------------------------

    @Test
    void tesGetUserIdByEmail_Success() {
        // Arrange
        UserIdRequest request = new UserIdRequest(user.getEmail());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        UUID id = userService.getUserIdByEmail(request);

        // Assert
        assertNotNull(id);
        assertEquals(userId, id);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void testGetUserIdByEmail_NonExistentUser() {
        // Arrange
        String nonExistentUserEmail = "some@email.com";
        UserIdRequest request = new UserIdRequest(nonExistentUserEmail);

        when(userRepository.findByEmail(nonExistentUserEmail)).thenReturn(Optional.empty());

        // Assert & Act
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserIdByEmail(request));

        // Assert
        verify(userRepository, times(1)).findByEmail(nonExistentUserEmail);
    }

    // ------------------------------------

    @Test
    void testGetUserRole_Success() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Assert & Act
        UserRole role = userService.getUserRole(userId);

        // Assert
        assertNotNull(role);
        assertEquals(user.getRole(), role);

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserRole_NonExistentUser() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Assert & Act
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserRole(nonExistentId));

        // Assert
        verify(userRepository, times(1)).findById(nonExistentId);
    }

    // ------------------------------------

}

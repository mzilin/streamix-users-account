package com.mariuszilinskas.vsp.users.account.service;

import com.mariuszilinskas.vsp.users.account.client.AuthFeignClient;
import com.mariuszilinskas.vsp.users.account.dto.*;
import com.mariuszilinskas.vsp.users.account.enums.UserRole;
import com.mariuszilinskas.vsp.users.account.enums.UserStatus;
import com.mariuszilinskas.vsp.users.account.exception.EmailExistsException;
import com.mariuszilinskas.vsp.users.account.exception.PasswordValidationException;
import com.mariuszilinskas.vsp.users.account.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.users.account.model.User;
import com.mariuszilinskas.vsp.users.account.producer.RabbitMQProducer;
import com.mariuszilinskas.vsp.users.account.repository.UserRepository;
import com.mariuszilinskas.vsp.users.account.util.TestUtils;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthFeignClient authFeignClient;

    @Mock
    private RabbitMQProducer rabbitMQProducer;

    @InjectMocks
    private UserServiceImpl userService;

    private CreateUserRequest createUserRequest;
    private final FeignException feignException = TestUtils.createFeignException();
    private final UUID userId = UUID.randomUUID();
    private final User user = new User();

    // ------------------------------------

    @BeforeEach
    void setUp() {
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setCountry("United Kingdom");
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(List.of(UserRole.USER));
        user.setAuthorities(List.of());

        createUserRequest = new CreateUserRequest(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCountry(),
                "Password123!"
        );
    }

    // ------------------------------------

    @Test
    void testCreateUser_Success() {
        // Arrange
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        var credentialsRequest = new CredentialsRequest(
                userId, createUserRequest.firstName(), createUserRequest.email(), createUserRequest.password());
        var profileRequest = new CreateUserDefaultProfileRequest(userId, user.getFirstName());

        when(userRepository.existsByEmail(createUserRequest.email()))
                .thenReturn(false);
        when(userRepository.save(captor.capture())).thenReturn(user);
        doNothing().when(rabbitMQProducer).sendCreateCredentialsMessage(credentialsRequest);
        doNothing().when(rabbitMQProducer).sendCreateUserDefaultProfileMessage(profileRequest);

        // Act
        UserResponse response = userService.createUser(createUserRequest);

        // Assert
        assertNotNull(response);
        assertEquals(user.getId(), response.id());

        verify(userRepository, times(1)).existsByEmail(createUserRequest.email());
        verify(userRepository, times(1)).save(captor.capture());
        verify(rabbitMQProducer, times(1)).sendCreateCredentialsMessage(credentialsRequest);
        verify(rabbitMQProducer, times(1)).sendCreateUserDefaultProfileMessage(profileRequest);

        User savedUser = captor.getValue();
        assertEquals(createUserRequest.firstName(), savedUser.getFirstName());
        assertEquals(createUserRequest.lastName(), savedUser.getLastName());
        assertEquals(createUserRequest.email(), savedUser.getEmail());
        assertEquals(createUserRequest.country(), savedUser.getCountry());
        assertEquals(UserStatus.PENDING, savedUser.getStatus());
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
        verify(rabbitMQProducer, never()).sendCreateCredentialsMessage(any(CredentialsRequest.class));
        verify(rabbitMQProducer, never()).sendCreateUserDefaultProfileMessage(any(CreateUserDefaultProfileRequest.class));
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
        UpdateUserRequest request = new UpdateUserRequest("UpdatedFirstName", "UpdatedLastName", "UpdatedCountry");

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
        assertEquals(request.country(), savedUser.getCountry());
    }

    @Test
    void testUpdateUser_NonExistentUser() {
        // Arrange
        UpdateUserRequest request = new UpdateUserRequest("UpdatedFirstName", "UpdatedLastName", "UpdatedCountry");

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

        var emailRequest = new UpdateEmailRequest(newEmail, password);
        var passwordRequest = new VerifyPasswordRequest(userId, password);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(authFeignClient.verifyPassword(passwordRequest)).thenReturn(null);
        when(userRepository.existsByEmail(emailRequest.email())).thenReturn(false);
        when(userRepository.save(captor.capture())).thenReturn(user);
        doNothing().when(rabbitMQProducer).sendResetPasscodeMessage(userId);

        // Act
        UpdateEmailResponse response = userService.updateUserEmail(userId, emailRequest);

        // Assert
        assertNotNull(response);
        assertEquals(user.getId(), response.userId());
        assertEquals(newEmail, response.email());
        assertFalse(response.isEmailVerified());

        verify(userRepository, times(1)).findById(userId);
        verify(authFeignClient, times(1)).verifyPassword(passwordRequest);
        verify(userRepository, times(1)).existsByEmail(newEmail);
        verify(userRepository, times(1)).save(captor.capture());
        verify(rabbitMQProducer, times(1)).sendResetPasscodeMessage(userId);

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

        var emailRequest = new UpdateEmailRequest(newEmail, password);
        var passwordRequest = new VerifyPasswordRequest(userId, password);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doThrow(feignException).when(authFeignClient).verifyPassword(passwordRequest);

        // Act & Assert
        assertThrows(PasswordValidationException.class, () -> userService.updateUserEmail(userId, emailRequest));

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(authFeignClient, times(1)).verifyPassword(passwordRequest);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(rabbitMQProducer, never()).sendResetPasscodeMessage(any(UUID.class));
    }

    @Test
    void testUpdateUserEmail_NonExistentUser() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        String newEmail = "new_email@example.com";
        var emailRequest = new UpdateEmailRequest(newEmail, "Password1!");

        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Assert & Act
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUserEmail(nonExistentId, emailRequest));

        // Assert
        verify(userRepository, times(1)).findById(nonExistentId);
        verify(authFeignClient, never()).verifyPassword(any(VerifyPasswordRequest.class));
        verify(userRepository, never()).existsByEmail(newEmail);
        verify(userRepository, never()).save(any(User.class));
        verify(rabbitMQProducer, never()).sendResetPasscodeMessage(any(UUID.class));
    }

    @Test
    void testUpdateUserEmail_NewEmailAlreadyInUse() {
        // Arrange
        String password = "Password1!";
        String newEmail = "email_in_use@example.com";

        var emailRequest = new UpdateEmailRequest(newEmail, password);
        var passwordRequest = new VerifyPasswordRequest(userId, password);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(authFeignClient.verifyPassword(passwordRequest)).thenReturn(null);
        when(userRepository.existsByEmail(newEmail)).thenReturn(true);

        // Assert & Act
        assertThrows(EmailExistsException.class, () -> userService.updateUserEmail(userId, emailRequest));

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(authFeignClient, times(1)).verifyPassword(passwordRequest);
        verify(userRepository, times(1)).existsByEmail(newEmail);
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
    void tesGetUserAuthDetailsWithEmail_Success() {
        // Arrange
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        AuthDetailsResponse response = userService.getUserAuthDetailsByEmail(user.getEmail());

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.userId());
        assertThat(user.getRoles()).containsExactlyInAnyOrderElementsOf(response.roles());
        assertThat(user.getAuthorities()).containsExactlyInAnyOrderElementsOf(response.authorities());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime savedUserLastActive = savedUser.getLastActive();
        long secondsDifference = Math.abs(ChronoUnit.SECONDS.between(now, savedUserLastActive));
        assertTrue(secondsDifference <= 1); // 1 second tolerance
    }

    @Test
    void testGetUserAuthDetailsWithEmail_NonExistentUser() {
        // Arrange
        String nonExistentUserEmail = "some@email.com";
        when(userRepository.findByEmail(nonExistentUserEmail)).thenReturn(Optional.empty());

        // Assert & Act
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserAuthDetailsByEmail(nonExistentUserEmail));

        // Assert
        verify(userRepository, times(1)).findByEmail(nonExistentUserEmail);
        verify(userRepository, never()).save(any(User.class));
    }

    // ------------------------------------

    @Test
    void tesGetUserAuthDetailsWithId_Success() {
        // Arrange
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(captor.capture())).thenReturn(user);

        // Act
        AuthDetailsResponse response = userService.getUserAuthDetailsByUserId(userId);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.userId());
        assertThat(user.getRoles()).containsExactlyInAnyOrderElementsOf(response.roles());
        assertThat(user.getAuthorities()).containsExactlyInAnyOrderElementsOf(response.authorities());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime savedUserLastActive = savedUser.getLastActive();
        long secondsDifference = Math.abs(ChronoUnit.SECONDS.between(now, savedUserLastActive));
        assertTrue(secondsDifference <= 1); // 1 second tolerance
    }

    @Test
    void testGetUserAuthDetailsWithId_NonExistentUser() {
        // Arrange
        UUID nonExistentUserEId = UUID.randomUUID();
        when(userRepository.findById(nonExistentUserEId)).thenReturn(Optional.empty());

        // Assert & Act
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserAuthDetailsByUserId(nonExistentUserEId));

        // Assert
        verify(userRepository, times(1)).findById(nonExistentUserEId);
        verify(userRepository, never()).save(any(User.class));
    }

    // ------------------------------------

    @Test
    void testDeleteUser_Success() {
        // Arrange
        String password = "Password1!";
        var deleteRequest = new DeleteUserRequest(password);
        var passwordRequest = new VerifyPasswordRequest(userId, password);

        when(authFeignClient.verifyPassword(passwordRequest)).thenReturn(null);
        doNothing().when(userRepository).deleteById(userId);
        doNothing().when(rabbitMQProducer).sendDeleteUserDataMessage(userId);

        // Act
        userService.deleteUser(userId, deleteRequest);

        // Assert
        verify(authFeignClient, times(1)).verifyPassword(passwordRequest);
        verify(userRepository, times(1)).deleteById(userId);
        verify(rabbitMQProducer, times(1)).sendDeleteUserDataMessage(userId);
    }

    @Test
    void testDeleteUser_WrongPassword() {
        // Arrange
        String password = "Password1!";
        var deleteRequest = new DeleteUserRequest(password);
        var passwordRequest = new VerifyPasswordRequest(userId, password);

        doThrow(feignException).when(authFeignClient).verifyPassword(passwordRequest);

        // Act & Assert
        assertThrows(PasswordValidationException.class, () -> userService.deleteUser(userId, deleteRequest));

        // Assert
        verify(authFeignClient, times(1)).verifyPassword(passwordRequest);
        verify(userRepository, never()).deleteById(any(UUID.class));
        verify(rabbitMQProducer, never()).sendDeleteUserDataMessage(any(UUID.class));
    }

    @Test
    void testDeleteUser_NonExistentUser() {
        // Arrange
        UUID nonExtentUserId = UUID.randomUUID();
        String password = "Password1!";
        var deleteRequest = new DeleteUserRequest(password);
        var passwordRequest = new VerifyPasswordRequest(nonExtentUserId, password);

        doThrow(feignException).when(authFeignClient).verifyPassword(passwordRequest);

        // Act & Assert
        assertThrows(PasswordValidationException.class, () -> userService.deleteUser(nonExtentUserId, deleteRequest));

        // Assert
        verify(authFeignClient, times(1)).verifyPassword(passwordRequest);
        verify(userRepository, never()).deleteById(any(UUID.class));
        verify(rabbitMQProducer, never()).sendDeleteUserDataMessage(any(UUID.class));
    }





}

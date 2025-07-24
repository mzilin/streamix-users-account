package com.mariuszilinskas.vsp.users.account.mapper;

import com.mariuszilinskas.vsp.users.account.dto.*;
import com.mariuszilinskas.vsp.users.account.enums.UserRole;
import com.mariuszilinskas.vsp.users.account.enums.UserStatus;
import com.mariuszilinskas.vsp.users.account.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

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
    }

    // ------------------------------------

    @Test
    void testMapFromCreateRequest_Success() {
        // Arrange
        var request = new CreateUserRequest(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCountry(),
                "Password123!"
        );

        // Act
        User createdUser = UserMapper.mapFromCreateRequest(request);

        // Assert
        assertEquals(user.getFirstName(), createdUser.getFirstName());
        assertEquals(user.getLastName(), createdUser.getLastName());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getCountry(), createdUser.getCountry());
        assertEquals(List.of(UserRole.USER), createdUser.getRoles());
        assertEquals(UserStatus.PENDING, createdUser.getStatus());
    }

    @Test
    void testApplyUpdates_Success() {
        // Arrange
        String newFirstName = "Paul";
        String newLastName = "Stevens";
        String newCountry = "England";

        var request = new UpdateUserRequest(newFirstName, newLastName, newCountry);

        // Act
        UserMapper.applyUpdates(user, request);

        // Assert
        assertEquals(user.getFirstName(), newFirstName);
        assertEquals(user.getLastName(), newLastName);
        assertEquals(user.getCountry(), newCountry);
    }

    @Test
    void testMapToUserResponse_Success() {
        // Act
        UserResponse response = UserMapper.mapToUserResponse(user);

        // Assert
        assertEquals(userId, response.id());
        assertEquals(user.getFirstName(), response.firstName());
        assertEquals(user.getLastName(), response.lastName());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getCountry(), response.country());
        assertEquals(user.getStatus().name(), response.status());
    }

    @Test
    void testMapToUpdateEmailResponse_Success() {
        // Arrange
        user.setEmailVerified(true);

        // Act
        UpdateEmailResponse response = UserMapper.mapToUpdateEmailResponse(user);

        // Assert
        assertEquals(userId, response.userId());
        assertEquals(user.getEmail(), response.email());
        assertTrue(response.isEmailVerified());
    }

    @Test
    void testMapToAuthDetailsResponse_Success() {
        // Act
        AuthDetailsResponse response = UserMapper.mapToAuthDetailsResponse(user);

        // Assert
        assertEquals(userId, response.userId());
        assertEquals(user.getRoles(), response.roles());
        assertEquals(user.getAuthorities(), response.authorities());
        assertEquals(user.getStatus(), response.status());
    }

    @Test
    void testMapToCredentialsRequest_Success() {
        // Arrange
        String password = "Secret!123";

        // Act
        CredentialsRequest request = UserMapper.mapToCredentialsRequest(user, password);

        // Assert
        assertEquals(userId, request.userId());
        assertEquals(user.getFirstName(), request.firstName());
        assertEquals(user.getEmail(), request.email());
        assertEquals(password, request.password());
    }

    @Test
    void testMapToDefaultProfileRequest_Success() {
        // Act
        CreateUserDefaultProfileRequest profileRequest = UserMapper.mapToDefaultProfileRequest(user);

        // Assert
        assertEquals(userId, profileRequest.userId());
        assertEquals(user.getFirstName(), profileRequest.firstName());
    }

}

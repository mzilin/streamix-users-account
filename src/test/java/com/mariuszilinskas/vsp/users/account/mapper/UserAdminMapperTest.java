package com.mariuszilinskas.vsp.users.account.mapper;

import com.mariuszilinskas.vsp.users.account.dto.*;
import com.mariuszilinskas.vsp.users.account.enums.UserRole;
import com.mariuszilinskas.vsp.users.account.enums.UserStatus;
import com.mariuszilinskas.vsp.users.account.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAdminMapperTest {

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
        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(List.of(UserRole.USER));
        user.setAuthorities(List.of());
        user.setCreatedAt(ZonedDateTime.now());
        user.setLastActive(ZonedDateTime.now());
    }

    // ------------------------------------


    @Test
    void shouldMapToUserResponse() {
        // Act
        UserAdminResponse response = UserAdminMapper.mapToUserAdminResponse(user);

        // Assert
        assertEquals(userId, response.id());
        assertEquals(user.getFirstName(), response.firstName());
        assertEquals(user.getLastName(), response.lastName());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getCountry(), response.country());
        assertEquals(user.isEmailVerified(), response.isEmailVerified());
        assertEquals(user.getStatus().name(), response.status());
        assertEquals(user.getRoles(), response.roles());
        assertEquals(user.getAuthorities(), response.authorities());
        assertEquals(user.getCreatedAt(), response.createdAt());
        assertEquals(user.getLastActive(), response.lastActive());
    }

}

package com.mariuszilinskas.vsp.users.account.controller;

import com.mariuszilinskas.vsp.users.account.dto.UserAdminResponse;
import com.mariuszilinskas.vsp.users.account.enums.UserAuthority;
import com.mariuszilinskas.vsp.users.account.enums.UserRole;
import com.mariuszilinskas.vsp.users.account.enums.UserStatus;
import com.mariuszilinskas.vsp.users.account.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.users.account.model.User;
import com.mariuszilinskas.vsp.users.account.service.UserAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAdminController.class)
public class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserAdminService userAdminService;

    private UUID userId;
    private final UUID nonExistentId = UUID.randomUUID();
    private UserAdminResponse userAdminResponse;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        userAdminResponse = new UserAdminResponse(
                userId,
                "John",
                "Doe",
                "john@example.com",
                "UK",
                true,
                "ACTIVE",
                List.of(UserRole.USER),
                List.of(),
                null,
                null
        );
    }

    @Test
    void testGetUsers_Success() throws Exception {
        // Arrange
        when(userAdminService.getUsers()).thenReturn(List.of(userAdminResponse));

        // Act & Assert
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userId.toString()))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));
    }

    @Test
    void testGrantUserRole_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/admin/{userId}/role/{userRole}", userId, UserRole.ADMIN))
                .andExpect(status().isNoContent());

        verify(userAdminService).grantUserRole(userId, UserRole.ADMIN);
    }

    @Test
    void testGrantUserRole_NotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException(User.class, "id", nonExistentId))
                .when(userAdminService).grantUserRole(nonExistentId, UserRole.ADMIN);

        // Act & Assert
        mockMvc.perform(post("/admin/{userId}/role/{userRole}", nonExistentId, UserRole.ADMIN))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveUserRole_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/admin/{userId}/role/{userRole}", userId, UserRole.USER))
                .andExpect(status().isNoContent());

        verify(userAdminService).removeUserRole(userId, UserRole.USER);
    }

    @Test
    void testRemoveUserRole_NotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException(User.class, "id", nonExistentId))
                .when(userAdminService).removeUserRole(nonExistentId, UserRole.ADMIN);

        // Act & Assert
        mockMvc.perform(delete("/admin/{userId}/role/{userRole}", nonExistentId, UserRole.ADMIN))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGrantUserAuthority_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/admin/{userId}/authority/{authority}", userId, UserAuthority.MANAGE_SETTINGS))
                .andExpect(status().isNoContent());

        verify(userAdminService).grantUserAuthority(userId, UserAuthority.MANAGE_SETTINGS);
    }

    @Test
    void testGrantUserAuthority_NotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException(User.class, "id", nonExistentId))
                .when(userAdminService).grantUserAuthority(nonExistentId, UserAuthority.MANAGE_SETTINGS);

        // Act & Assert
        mockMvc.perform(post("/admin/{userId}/authority/{authority}", nonExistentId, UserAuthority.MANAGE_SETTINGS))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveUserAuthority_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/admin/{userId}/authority/{authority}", userId, UserAuthority.MANAGE_SETTINGS))
                .andExpect(status().isNoContent());

        verify(userAdminService).removeUserAuthority(userId, UserAuthority.MANAGE_SETTINGS);
    }

    @Test
    void testRemoveUserAuthority_NotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException(User.class, "id", nonExistentId))
                .when(userAdminService).removeUserAuthority(nonExistentId, UserAuthority.MANAGE_SETTINGS);

        // Act & Assert
        mockMvc.perform(delete("/admin/{userId}/authority/{authority}", nonExistentId, UserAuthority.MANAGE_SETTINGS))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUserStatus_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/admin/{userId}/status/{status}", userId, UserStatus.SUSPENDED))
                .andExpect(status().isNoContent());

        verify(userAdminService).updateUserStatus(userId, UserStatus.SUSPENDED);
    }

    @Test
    void testUpdateUserStatus_NotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException(User.class, "id", nonExistentId))
                .when(userAdminService).updateUserStatus(nonExistentId, UserStatus.SUSPENDED);

        // Act & Assert
        mockMvc.perform(patch("/admin/{userId}/status/{status}", nonExistentId, UserStatus.SUSPENDED))
                .andExpect(status().isNotFound());
    }

}

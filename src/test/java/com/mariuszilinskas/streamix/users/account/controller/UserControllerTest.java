package com.mariuszilinskas.streamix.users.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mariuszilinskas.streamix.users.account.dto.*;
import com.mariuszilinskas.streamix.users.account.enums.UserStatus;
import com.mariuszilinskas.streamix.users.account.exception.ResourceNotFoundException;
import com.mariuszilinskas.streamix.users.account.model.User;
import com.mariuszilinskas.streamix.users.account.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static com.mariuszilinskas.streamix.users.account.constant.RequestValidationMessages.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID userId = UUID.randomUUID();
    private final UUID nonExistentId = UUID.randomUUID();
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private UpdateEmailRequest updateEmailRequest;
    private DeleteUserRequest deleteUserRequest;
    private UserResponse userResponse;
    private UpdateEmailResponse updateEmailResponse;
    private AuthDetailsResponse authDetailsResponse;

    @BeforeEach
    void setup() {
        createUserRequest = new CreateUserRequest("John", "Doe", "john@example.com", "UK", "Password123!");
        updateUserRequest = new UpdateUserRequest("Johnny", "Doeson", "UK");
        updateEmailRequest = new UpdateEmailRequest("new@example.com", "Password123");
        deleteUserRequest = new DeleteUserRequest("Password123");

        userResponse = new UserResponse(userId, "John", "Doe", "john@example.com", "UK", "ACTIVE");
        updateEmailResponse = new UpdateEmailResponse(userId, "new@example.com", false);
        authDetailsResponse = new AuthDetailsResponse(userId, List.of(), List.of(), UserStatus.ACTIVE);
    }

    @Test
    void testCreateUser_Success() throws Exception {
        // Arrange
        when(userService.createUser(any())).thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }

    @Test
    void testCreateUser_RequiredFieldsAreNull() throws Exception {
        // Arrange
        var invalidRequest = new CreateUserRequest(null, null, null, null, null);

        // Act & Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.firstName").value("firstName " + CANNOT_BE_BLANK))
                .andExpect(jsonPath("$.fieldErrors.lastName").value("lastName " + CANNOT_BE_BLANK))
                .andExpect(jsonPath("$.fieldErrors.email").value("email " + CANNOT_BE_BLANK))
                .andExpect(jsonPath("$.fieldErrors.country").value("country " + CANNOT_BE_BLANK))
                .andExpect(jsonPath("$.fieldErrors.password").value("password " + CANNOT_BE_BLANK));
    }

    @Test
    void testCreateUser_RequiredFieldsAreBlank() throws Exception {
        // Arrange
        var invalidRequest = new CreateUserRequest(" ", " ", " ", " ", null);

        // Act & Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.firstName").value("firstName " + CANNOT_BE_BLANK))
                .andExpect(jsonPath("$.fieldErrors.lastName").value("lastName " + CANNOT_BE_BLANK))
                .andExpect(jsonPath("$.fieldErrors.email").value("email " + CANNOT_BE_BLANK))
                .andExpect(jsonPath("$.fieldErrors.country").value("country " + CANNOT_BE_BLANK))
                .andExpect(jsonPath("$.fieldErrors.password").value("password " + CANNOT_BE_BLANK));
    }

    @Test
    void testCreateUser_InvalidEmail() throws Exception {
        // Arrange
        var invalidRequest = new CreateUserRequest("John", "Doe", "john.example.com", "UK", "Password123");

        // Act & Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").value(INVALID_EMAIL));
    }

    @Test
    void testCreateUser_PasswordTooShort() throws Exception {
        // Arrange
        var invalidRequest = new CreateUserRequest("John", "Doe", "john.example.com", "UK", "aA1!");

        // Act & Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").value(PASSWORD_INCORRECT_LENGTH));
    }

    @Test
    void testCreateUser_PasswordTooLong() throws Exception {
        // Arrange
        var longPass = "Aa1!".repeat(17); // 68 characters
        var invalidRequest = new CreateUserRequest("John", "Doe", "john@example.com", "UK", longPass);

        // Act & Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").value(PASSWORD_INCORRECT_LENGTH));
    }

    @Test
    void testCreateUser_PasswordNoLowercase() throws Exception {
        // Arrange
        var invalidRequest = new CreateUserRequest("John", "Doe", "john@example.com", "UK", "PASSWORD1!");

        // Act & Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").value(PASSWORD_MISSING_LOWERCASE));
    }

    @Test
    void testCreateUser_PasswordNoUppercase() throws Exception {
        // Arrange
        var invalidRequest = new CreateUserRequest("John", "Doe", "john@example.com", "UK", "password1!");

        // Act & Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").value(PASSWORD_MISSING_UPPERCASE));
    }

    @Test
    void testCreateUser_PasswordNoDigit() throws Exception {
        // Arrange
        var invalidRequest = new CreateUserRequest("John", "Doe", "john@example.com", "UK", "Password!");

        // Act & Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").value(PASSWORD_MISSING_DIGIT));
    }

    @Test
    void testCreateUser_PasswordNoSpecialChar() throws Exception {
        // Arrange
        var invalidRequest = new CreateUserRequest("John", "Doe", "john@example.com", "UK", "Password1");

        // Act & Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").value(PASSWORD_MISSING_SPECIAL));
    }

    @Test
    void testGetUser_Success() throws Exception {
        // Arrange
        when(userService.getUser(userId)).thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(get("/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testGetUser_NotFound() throws Exception {
        // Arrange
        when(userService.getUser(nonExistentId))
                .thenThrow(new ResourceNotFoundException(User.class, "id", nonExistentId));

        // Act & Assert
        mockMvc.perform(get("/user/{userId}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        // Arrange
        when(userService.updateUser(eq(userId), any())).thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(put("/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testUpdateUser_RequiredFieldsAreNull() throws Exception {
        // Arrange
        var invalidRequest = new UpdateUserRequest(null, null, null);

        // Act & Assert
        mockMvc.perform(put("/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.firstName").value("firstName " + CANNOT_BE_BLANK))
                .andExpect(jsonPath("$.fieldErrors.lastName").value("lastName " + CANNOT_BE_BLANK))
                .andExpect(jsonPath("$.fieldErrors.country").value("country " + CANNOT_BE_BLANK));
    }

    @Test
    void testUpdateUser_RequiredFieldsAreBlank() throws Exception {
        // Arrange
        var invalidRequest = new UpdateUserRequest(" ", " ", " ");

        // Act & Assert
        mockMvc.perform(put("/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.firstName").value("firstName " + CANNOT_BE_BLANK))
                .andExpect(jsonPath("$.fieldErrors.lastName").value("lastName " + CANNOT_BE_BLANK))
                .andExpect(jsonPath("$.fieldErrors.country").value("country " + CANNOT_BE_BLANK));
    }

    @Test
    void testUpdateUserEmail_Success() throws Exception {
        // Arrange
        when(userService.updateUserEmail(eq(userId), any())).thenReturn(updateEmailResponse);

        // Act & Assert
        mockMvc.perform(put("/user/{userId}/email", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateEmailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void testUpdateUserEmail_RequiredFieldsAreNull() throws Exception {
        // Arrange
        var invalidRequest = new UpdateEmailRequest(null, null);

        // Act & Assert
        mockMvc.perform(put("/user/{userId}/email", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").value("email " + CANNOT_BE_BLANK))
                .andExpect(jsonPath("$.fieldErrors.password").value("password " + CANNOT_BE_BLANK));
    }

    @Test
    void testUpdateUserEmail_RequiredFieldsAreBlank() throws Exception {
        // Arrange
        var invalidRequest = new UpdateEmailRequest(" ", " ");

        // Act & Assert
        mockMvc.perform(put("/user/{userId}/email", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").value("email " + CANNOT_BE_BLANK))
                .andExpect(jsonPath("$.fieldErrors.password").value("password " + CANNOT_BE_BLANK));
    }

    @Test
    void testUpdateUserEmail_InvalidEmail() throws Exception {
        // Arrange
        var invalidRequest = new UpdateEmailRequest("john.doe.email.com", "Password1!");

        // Act & Assert
        mockMvc.perform(put("/user/{userId}/email", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").value(INVALID_EMAIL));
    }

    @Test
    void testUpdateUserEmail_NotFound() throws Exception {
        // Arrange
        when(userService.updateUserEmail(eq(nonExistentId), any()))
                .thenThrow(new ResourceNotFoundException(User.class, "id", nonExistentId));

        // Act & Assert
        mockMvc.perform(put("/user/{userId}/email", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateEmailRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testVerifyUser_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/user/{userId}/verify", userId))
                .andExpect(status().isNoContent());

        verify(userService).verifyUser(userId);
    }

    @Test
    void testVerifyUser_NotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException(User.class, "id", nonExistentId))
                .when(userService).verifyUser(nonExistentId);

        // Act & Assert
        mockMvc.perform(patch("/user/{userId}/verify", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAuthDetailsByEmail_Success() throws Exception {
        // Arrange
        when(userService.getUserAuthDetailsByEmail("john@example.com")).thenReturn(authDetailsResponse);

        // Act & Assert
        mockMvc.perform(get("/user/auth-details/by-email")
                        .param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testGetAuthDetailsByEmail_NotFound() throws Exception {
        // Arrange
        String nonExistingEmail = "john@example.com";
        when(userService.getUserAuthDetailsByEmail(nonExistingEmail))
                .thenThrow(new ResourceNotFoundException(User.class, "email", nonExistingEmail));

        // Act & Assert
        mockMvc.perform(get("/user/auth-details/by-email")
                        .param("email", "john@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAuthDetailsByUserId_Success() throws Exception {
        // Arrange
        when(userService.getUserAuthDetailsByUserId(userId)).thenReturn(authDetailsResponse);

        // Act & Assert
        mockMvc.perform(get("/user/auth-details/by-userid")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testGetAuthDetailsByUserId_NotFound() throws Exception {
        // Arrange
        when(userService.getUserAuthDetailsByUserId(nonExistentId))
                .thenThrow(new ResourceNotFoundException(User.class, "id", nonExistentId));

        // Act & Assert
        mockMvc.perform(get("/user/auth-details/by-userid")
                        .param("userId", nonExistentId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteUserRequest)))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(eq(userId), any());
    }

    @Test
    void testDeleteUser_PasswordNull() throws Exception {
        // Arrange
        var invalidRequest = new DeleteUserRequest(null);

        doThrow(new ResourceNotFoundException(User.class, "id", userId))
                .when(userService).deleteUser(eq(userId), any());

        // Act & Assert
        mockMvc.perform(delete("/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").value("password " + CANNOT_BE_BLANK));
    }

    @Test
    void testDeleteUser_PasswordBlank() throws Exception {
        // Arrange
        var invalidRequest = new DeleteUserRequest(" ");

        doThrow(new ResourceNotFoundException(User.class, "id", userId))
                .when(userService).deleteUser(eq(userId), any());

        // Act & Assert
        mockMvc.perform(delete("/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").value("password " + CANNOT_BE_BLANK));
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException(User.class, "id", nonExistentId))
                .when(userService).deleteUser(eq(nonExistentId), any());

        // Act & Assert
        mockMvc.perform(delete("/user/{userId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteUserRequest)))
                .andExpect(status().isNotFound());
    }

}

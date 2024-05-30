package com.mariuszilinskas.vsp.userservice.service;

import com.mariuszilinskas.vsp.userservice.dto.*;
import com.mariuszilinskas.vsp.userservice.enums.UserRole;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUser(UUID userId);

    UserResponse updateUser(UUID userId, UpdateUserRequest request);

    UpdateEmailResponse updateUserEmail(UUID userId, UpdateEmailRequest request);

    void verifyUser(UUID userId);

    UUID getUserIdByEmail(String email);

    UserRole getUserRole(UUID userId);

    void deleteUser(UUID userId, DeleteUserRequest request);

}

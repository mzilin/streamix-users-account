package com.mariuszilinskas.vsp.userservice.service;

import com.mariuszilinskas.vsp.userservice.dto.*;
import com.mariuszilinskas.vsp.userservice.enums.UserRole;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUser(UUID userId);

    UserResponse updateUser(UUID userId, UpdateUserRequest request);

    UpdateEmailResponse updateUserEmail(UUID userId, UpdateEmailRequest request);

    void verifyUserEmail(UUID userId);

    UUID getUserIdByEmail(UserIdRequest request);

    UserRole getUserRole(UUID userId);

}

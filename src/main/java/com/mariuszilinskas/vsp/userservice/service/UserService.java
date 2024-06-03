package com.mariuszilinskas.vsp.userservice.service;

import com.mariuszilinskas.vsp.userservice.dto.*;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUser(UUID userId);

    UserResponse updateUser(UUID userId, UpdateUserRequest request);

    UpdateEmailResponse updateUserEmail(UUID userId, UpdateEmailRequest request);

    void verifyUser(UUID userId);

    AuthDetailsResponse getUserAuthDetails(String email);

    void deleteUser(UUID userId, DeleteUserRequest request);

}

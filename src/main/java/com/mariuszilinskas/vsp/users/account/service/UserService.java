package com.mariuszilinskas.vsp.users.account.service;

import com.mariuszilinskas.vsp.users.account.dto.*;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUser(UUID userId);

    UserResponse updateUser(UUID userId, UpdateUserRequest request);

    UpdateEmailResponse updateUserEmail(UUID userId, UpdateEmailRequest request);

    void verifyUser(UUID userId);

    AuthDetailsResponse getUserAuthDetailsByEmail(String email);

    AuthDetailsResponse getUserAuthDetailsByUserId(UUID id);

    void deleteUser(UUID userId, DeleteUserRequest request);

}

package com.mariuszilinskas.vsp.userservice.service;

import com.mariuszilinskas.vsp.userservice.dto.CreateUserRequest;
import com.mariuszilinskas.vsp.userservice.dto.UserIdRequest;
import com.mariuszilinskas.vsp.userservice.dto.UserResponse;
import com.mariuszilinskas.vsp.userservice.enums.UserRole;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUser(UUID userId);

    void verifyUserEmail(UUID userId);

    UUID getUserIdByEmail(UserIdRequest request);

    UserRole getUserRole(UUID userId);

}

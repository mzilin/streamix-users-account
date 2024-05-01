package com.mariuszilinskas.vsp.userservice.service;

import com.mariuszilinskas.vsp.userservice.dto.CreateUserRequest;
import com.mariuszilinskas.vsp.userservice.dto.UserResponse;
import com.mariuszilinskas.vsp.userservice.enums.UserStatus;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    void updateUserStatus(UUID userId, UserStatus newStatus);

}

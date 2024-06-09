package com.mariuszilinskas.vsp.userservice.util;

import com.mariuszilinskas.vsp.userservice.dto.UserResponse;
import com.mariuszilinskas.vsp.userservice.model.User;

public abstract class UserUtils {

    private UserUtils() {
        // Private constructor to prevent instantiation
    }

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd hh:mm:ss";

    public static UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCountry(),
                user.isEmailVerified(),
                user.getStatus().name(),
                user.getRoles(),
                user.getAuthorities(),
                user.getCreatedAt(),
                user.getLastActive()
        );
    }

}

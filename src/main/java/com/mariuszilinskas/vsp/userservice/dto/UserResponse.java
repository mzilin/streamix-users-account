package com.mariuszilinskas.vsp.userservice.dto;

import com.mariuszilinskas.vsp.userservice.model.UserProfile;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        boolean isEmailVerified,
        String status,
        String role,
        List<UserProfile> userProfiles,
        ZonedDateTime createdAt,
        ZonedDateTime lastActive
) {}

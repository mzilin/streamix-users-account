package com.mariuszilinskas.vsp.userservice.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String country,
        boolean isEmailVerified,
        String status,
        List<String> roles,
        List<String> authorities,
        ZonedDateTime createdAt,
        ZonedDateTime lastActive
) {}

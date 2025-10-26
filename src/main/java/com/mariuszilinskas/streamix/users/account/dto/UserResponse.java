package com.mariuszilinskas.streamix.users.account.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String country,
        String status
) {}

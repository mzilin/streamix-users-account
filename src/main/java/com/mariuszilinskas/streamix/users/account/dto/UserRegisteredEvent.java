package com.mariuszilinskas.streamix.users.account.dto;

import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String country,
        String passwordHash
) {}

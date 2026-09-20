package com.mariuszilinskas.streamix.users.account.dto;

import java.util.UUID;

public record SetupCredentialsRequest(
        UUID userId,
        String firstName,
        String email,
        String passwordHash
) {}

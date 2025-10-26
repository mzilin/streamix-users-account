package com.mariuszilinskas.streamix.users.account.dto;

import java.util.UUID;

public record CredentialsRequest(
        UUID userId,
        String firstName,
        String email,
        String password
) {}

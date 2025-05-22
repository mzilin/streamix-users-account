package com.mariuszilinskas.vsp.users.account.dto;

import java.util.UUID;

public record CredentialsRequest(
        UUID userId,
        String firstName,
        String email,
        String password
) {}

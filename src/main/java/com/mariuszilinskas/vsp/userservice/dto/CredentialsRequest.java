package com.mariuszilinskas.vsp.userservice.dto;

import java.util.UUID;

public record CredentialsRequest(
        UUID userId,
        String password
) {}

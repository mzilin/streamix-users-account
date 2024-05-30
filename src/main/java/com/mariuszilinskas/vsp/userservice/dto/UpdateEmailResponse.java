package com.mariuszilinskas.vsp.userservice.dto;

import java.util.UUID;

public record UpdateEmailResponse(
        UUID userId,
        String email,
        boolean isEmailVerified
) {}

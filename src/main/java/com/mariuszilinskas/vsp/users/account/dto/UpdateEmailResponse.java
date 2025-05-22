package com.mariuszilinskas.vsp.users.account.dto;

import java.util.UUID;

public record UpdateEmailResponse(
        UUID userId,
        String email,
        boolean isEmailVerified
) {}

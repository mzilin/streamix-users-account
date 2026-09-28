package com.mariuszilinskas.streamix.users.account.dto;

import java.util.UUID;

public record VerificationEmailRequest(
        UUID userId,
        String email,
        String firstName,
        String verificationToken
) {}

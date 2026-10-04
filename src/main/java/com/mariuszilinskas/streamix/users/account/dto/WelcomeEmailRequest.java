package com.mariuszilinskas.streamix.users.account.dto;

import java.util.UUID;

public record WelcomeEmailRequest(
        UUID userId,
        String email,
        String firstName
) {}

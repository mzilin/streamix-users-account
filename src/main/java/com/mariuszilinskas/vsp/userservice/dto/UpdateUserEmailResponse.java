package com.mariuszilinskas.vsp.userservice.dto;

import java.util.UUID;

public record UpdateUserEmailResponse(
        UUID userId,
        String email
) {}

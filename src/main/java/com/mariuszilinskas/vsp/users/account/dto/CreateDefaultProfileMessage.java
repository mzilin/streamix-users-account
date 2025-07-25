package com.mariuszilinskas.vsp.users.account.dto;

import java.util.UUID;

public record CreateDefaultProfileMessage(
        UUID userId,
        String firstName
) {}

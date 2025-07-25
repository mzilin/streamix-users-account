package com.mariuszilinskas.vsp.users.account.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record UserLastActiveMessage(
        UUID userId,
        ZonedDateTime lastActive
) {}
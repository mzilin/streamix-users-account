package com.mariuszilinskas.streamix.users.account.dto;

import java.util.UUID;

public record UserVerifiedEvent(
        UUID userId
) {}

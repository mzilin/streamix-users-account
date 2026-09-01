package com.mariuszilinskas.streamix.users.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import static com.mariuszilinskas.streamix.web.constant.ValidationMessages.*;

public record VerifyPasswordRequest(

        @NotNull(message = "userId " + CANNOT_BE_NULL)
        UUID userId,

        @NotBlank(message = "password " + CANNOT_BE_BLANK)
        String password

) {}

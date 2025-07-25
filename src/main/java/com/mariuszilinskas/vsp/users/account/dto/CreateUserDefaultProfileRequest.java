package com.mariuszilinskas.vsp.users.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import static com.mariuszilinskas.vsp.users.account.constant.RequestValidationMessages.*;

public record CreateUserDefaultProfileRequest(

        @NotNull(message = "userId " + CANNOT_BE_NULL)
        UUID userId,

        @NotBlank(message = "firstName " + CANNOT_BE_BLANK)
        String firstName

){}

package com.mariuszilinskas.vsp.users.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateUserDefaultProfileRequest(

        @NotNull(message = "userId cannot be null")
        UUID userId,

        @NotBlank(message = "firstName cannot be blank")
        String firstName

){}

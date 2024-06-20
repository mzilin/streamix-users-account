package com.mariuszilinskas.vsp.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateDefaultUserProfileRequest(

        @NotNull(message = "userId cannot be null")
        UUID userId,

        @NotBlank(message = "firstName cannot be blank")
        String firstName

){}

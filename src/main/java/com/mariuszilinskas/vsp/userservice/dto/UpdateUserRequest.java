package com.mariuszilinskas.vsp.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank(message = "firstName cannot be blank")
        String firstName,

        @NotBlank(message = "lastName cannot be blank")
        String lastName

) {
        public UpdateUserRequest {
                if (firstName != null) firstName = firstName.trim();
                if (lastName != null) lastName = lastName.trim();
        }
}

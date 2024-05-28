package com.mariuszilinskas.vsp.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserIdRequest(
        @NotBlank(message = "email cannot be blank")
        @Email(message = "email should be valid")
        String email
) {
        public UserIdRequest {
                if (email != null) email = email.trim().toLowerCase();
        }
}

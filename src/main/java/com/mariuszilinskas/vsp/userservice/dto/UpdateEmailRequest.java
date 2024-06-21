package com.mariuszilinskas.vsp.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequest(

        @NotBlank(message = "email cannot be blank")
        @Email(message = "email should be valid")
        String email,

        @NotBlank(message = "password cannot be blank")
        String password

) {
        public UpdateEmailRequest {
                if (email != null) email = email.trim().toLowerCase();
                if (password != null) password = password.trim();
        }
}

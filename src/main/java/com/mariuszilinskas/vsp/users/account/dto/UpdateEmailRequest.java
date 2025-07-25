package com.mariuszilinskas.vsp.users.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import static com.mariuszilinskas.vsp.users.account.constant.RequestValidationMessages.*;

public record UpdateEmailRequest(

        @NotBlank(message = "email " + CANNOT_BE_BLANK)
        @Email(message = INVALID_EMAIL)
        String email,

        @NotBlank(message = "password " + CANNOT_BE_BLANK)
        String password

) {
        public UpdateEmailRequest {
                if (email != null) email = email.trim().toLowerCase();
                if (password != null) password = password.trim();
        }
}

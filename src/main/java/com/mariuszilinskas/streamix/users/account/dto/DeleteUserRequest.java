package com.mariuszilinskas.streamix.users.account.dto;

import jakarta.validation.constraints.NotBlank;

import static com.mariuszilinskas.streamix.users.account.constant.RequestValidationMessages.CANNOT_BE_BLANK;

public record DeleteUserRequest(

        @NotBlank(message = "password " + CANNOT_BE_BLANK)
        String password

) {
        public DeleteUserRequest {
                if (password != null) password = password.trim();
        }
}

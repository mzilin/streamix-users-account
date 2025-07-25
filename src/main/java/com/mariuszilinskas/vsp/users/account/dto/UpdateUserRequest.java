package com.mariuszilinskas.vsp.users.account.dto;

import jakarta.validation.constraints.NotBlank;

import static com.mariuszilinskas.vsp.users.account.constant.RequestValidationMessages.CANNOT_BE_BLANK;

public record UpdateUserRequest(

        @NotBlank(message = "firstName " + CANNOT_BE_BLANK)
        String firstName,

        @NotBlank(message = "lastName " + CANNOT_BE_BLANK)
        String lastName,

        @NotBlank(message = "country " + CANNOT_BE_BLANK)
        String country

) {
        public UpdateUserRequest {
                if (firstName != null) firstName = firstName.trim();
                if (lastName != null) lastName = lastName.trim();
        }
}

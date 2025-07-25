package com.mariuszilinskas.vsp.users.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.mariuszilinskas.vsp.users.account.constant.RequestValidationMessages.*;

public record CreateUserRequest(

        @NotBlank(message = "firstName " + CANNOT_BE_BLANK)
        String firstName,

        @NotBlank(message = "lastName " + CANNOT_BE_BLANK)
        String lastName,

        @NotBlank(message = "email " + CANNOT_BE_BLANK)
        @Email(message = INVALID_EMAIL)
        String email,

        @NotBlank(message = "country " + CANNOT_BE_BLANK)
        String country,

        @NotBlank(message = "password " + CANNOT_BE_BLANK)
        @Size(min = 8, max = 64, message = PASSWORD_INCORRECT_LENGTH)
        @Pattern.List({
                @Pattern(regexp = ".*[a-z].*", message = PASSWORD_MISSING_LOWERCASE),
                @Pattern(regexp = ".*[A-Z].*", message = PASSWORD_MISSING_UPPERCASE),
                @Pattern(regexp = ".*\\d.*", message = PASSWORD_MISSING_DIGIT),
                @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*", message = PASSWORD_MISSING_SPECIAL)
        })
        String password

) {
        public CreateUserRequest {
                if (firstName != null) firstName = firstName.trim();
                if (lastName != null) lastName = lastName.trim();
                if (email != null) email = email.trim().toLowerCase();
                if (password != null) password = password.trim();
        }
}

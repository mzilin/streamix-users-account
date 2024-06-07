package com.mariuszilinskas.vsp.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank(message = "firstName cannot be blank")
        String firstName,

        @NotBlank(message = "lastName cannot be blank")
        String lastName,

        @NotBlank(message = "email cannot be blank")
        @Email(message = "email should be valid")
        String email,

        @NotBlank(message = "country cannot be blank")
        String country,

        @NotBlank(message = "password cannot be blank")
        @Size(min = 8, max = 64, message = "password must be between 8 and 64 characters")
        @Pattern.List({
                @Pattern(regexp = ".*[a-z].*", message = "password must contain at least one lowercase letter"),
                @Pattern(regexp = ".*[A-Z].*", message = "password must contain at least one uppercase letter"),
                @Pattern(regexp = ".*\\d.*", message = "password must contain at least one digit"),
                @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*",
                        message = "password must contain at least one special character")
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

package com.mariuszilinskas.vsp.users.account.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(

        @NotBlank(message = "firstName cannot be blank")
        String firstName,

        @NotBlank(message = "lastName cannot be blank")
        String lastName,

        @NotBlank(message = "country cannot be blank")
        String country

) {
        public UpdateUserRequest {
                if (firstName != null) firstName = firstName.trim();
                if (lastName != null) lastName = lastName.trim();
        }
}

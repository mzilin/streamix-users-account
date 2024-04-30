package com.mariuszilinskas.vsp.userservice.exception;

import java.util.UUID;

public class UserAlreadyVerifiedException extends RuntimeException {

    public UserAlreadyVerifiedException(UUID userId) {
        super("User with ID '" + userId + "' is already verified");
    }
}

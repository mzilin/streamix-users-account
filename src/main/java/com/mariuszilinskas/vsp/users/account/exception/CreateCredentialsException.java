package com.mariuszilinskas.vsp.users.account.exception;

import java.util.UUID;

public class CreateCredentialsException extends RuntimeException {

    public CreateCredentialsException(UUID userId) {
        super(String.format("Failed to create User [userId = '%s'] credentials.", userId));
    }

}

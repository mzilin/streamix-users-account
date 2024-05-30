package com.mariuszilinskas.vsp.userservice.exception;

public class PasswordValidationException extends RuntimeException {

    public PasswordValidationException() {
        super("Password validation failed");
    }
}

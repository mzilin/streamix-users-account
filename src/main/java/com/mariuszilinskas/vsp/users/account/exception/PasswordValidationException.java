package com.mariuszilinskas.vsp.users.account.exception;

public class PasswordValidationException extends RuntimeException {

    public PasswordValidationException() {
        super("Password validation failed");
    }

}

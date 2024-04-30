package com.mariuszilinskas.vsp.userservice.exception;

import lombok.Getter;

public class PasswordValidationException extends RuntimeException {

    public PasswordValidationException() {
        super("Current password is incorrect");
    }
}

package com.mariuszilinskas.vsp.userservice.exception;

public class PinCodeDoesntMatchException extends RuntimeException {

    public PinCodeDoesntMatchException() {
        super("Incorrect pin code. Try again.");
    }
}

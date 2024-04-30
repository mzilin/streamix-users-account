package com.mariuszilinskas.vsp.userservice.exception;

public class PinCodeExpiredException extends RuntimeException {

    public PinCodeExpiredException() {
        super("Pin code has expired. Request a new pin.");
    }
}

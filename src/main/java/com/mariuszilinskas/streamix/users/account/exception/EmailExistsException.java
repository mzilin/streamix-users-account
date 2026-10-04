package com.mariuszilinskas.streamix.users.account.exception;

public class EmailExistsException extends RuntimeException {

    public EmailExistsException() {
        super("Unable to complete the request. Please check your details and try again..");
    }

}

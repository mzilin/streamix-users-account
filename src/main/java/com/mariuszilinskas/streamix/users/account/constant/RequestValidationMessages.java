package com.mariuszilinskas.streamix.users.account.constant;

public class RequestValidationMessages {
    public static final String CANNOT_BE_NULL = "cannot be null";
    public static final String CANNOT_BE_BLANK = "cannot be blank";
    public static final String INVALID_EMAIL = "email should be valid";
    public static final String PASSWORD_INCORRECT_LENGTH = "password must be between 8 and 64 characters";
    public static final String PASSWORD_MISSING_LOWERCASE = "password must contain at least one lowercase letter";
    public static final String PASSWORD_MISSING_UPPERCASE = "password must contain at least one uppercase letter";
    public static final String PASSWORD_MISSING_DIGIT = "password must contain at least one digit";
    public static final String PASSWORD_MISSING_SPECIAL = "password must contain at least one special character";
}

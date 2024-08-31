package com.account.exception;

public class CustomerIdMustBeProvided extends RuntimeException {
    public CustomerIdMustBeProvided(String message) {
        super(message);
    }

    public CustomerIdMustBeProvided(String message, Throwable cause) {
        super(message, cause);
    }
}

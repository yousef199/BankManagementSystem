package com.account.exception;

public class CannotActivateAccountException extends RuntimeException {
    public CannotActivateAccountException(String message) {
        super(message);
    }

    public CannotActivateAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}

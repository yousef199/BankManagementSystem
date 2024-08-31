package com.account.exception;

public class InvalidAccountTransferRequest extends RuntimeException {
    public InvalidAccountTransferRequest(String message) {
        super(message);
    }

    public InvalidAccountTransferRequest(String message, Throwable cause) {
        super(message, cause);
    }
}

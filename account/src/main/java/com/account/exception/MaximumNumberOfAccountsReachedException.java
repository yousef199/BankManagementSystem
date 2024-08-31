package com.account.exception;

public class MaximumNumberOfAccountsReachedException extends RuntimeException{
    public MaximumNumberOfAccountsReachedException(String message) {
        super(message);
    }

    public MaximumNumberOfAccountsReachedException(String message, Throwable cause) {
        super(message, cause);
    }
}

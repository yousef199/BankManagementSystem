package com.account.exception;

public class SalaryAccountAlreadyExistsException extends RuntimeException {
    public SalaryAccountAlreadyExistsException(String message) {
        super(message);
    }

    public SalaryAccountAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

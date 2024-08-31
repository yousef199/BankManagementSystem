package com.customer.exception;

public class InvalidCustomerDeleteReqeustException extends RuntimeException {
    public InvalidCustomerDeleteReqeustException(String message) {
        super(message);
    }

    public InvalidCustomerDeleteReqeustException(String message, Throwable cause) {
        super(message, cause);
    }
}

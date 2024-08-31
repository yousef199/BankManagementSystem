package com.customer.exception;

/**
 * @author YQadous
 * This exception is thrown when a customer delete request is invalid.
 */
public class InvalidCustomerDeleteReqeustException extends RuntimeException {
    public InvalidCustomerDeleteReqeustException(String message) {
        super(message);
    }

    public InvalidCustomerDeleteReqeustException(String message, Throwable cause) {
        super(message, cause);
    }
}

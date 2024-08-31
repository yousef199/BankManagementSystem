package com.customer.exception;

import com.clients.dto.ErrorResponseDTO;
import com.clients.dto.ValidationErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YQadous
 * Global exception handler for the Customer service.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles validation exceptions thrown during request processing.
     *
     * @param ex The MethodArgumentNotValidException instance.
     * @return A ResponseEntity containing details of the validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error occurred: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.debug("Field error - {}: {}", fieldName, errorMessage);
        });

        ValidationErrorDTO errorResponse = new ValidationErrorDTO(HttpStatus.BAD_REQUEST.value(), errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions when a customer is not found.
     *
     * @param ex The CustomerNotFoundException instance.
     * @return A ResponseEntity containing details of the error.
     */
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCustomerNotFound(CustomerNotFoundException ex) {
        log.error("Customer not found: {}", ex.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "Customer not found"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions when a customer deletion request is invalid.
     *
     * @param ex The InvalidCustomerDeleteReqeustException instance.
     * @return A ResponseEntity containing details of the error.
     */
    @ExceptionHandler(InvalidCustomerDeleteReqeustException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCustomerDeleteRequest(InvalidCustomerDeleteReqeustException ex) {
        log.error("Invalid customer delete request: {}", ex.getMessage());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Invalid customer delete request"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}

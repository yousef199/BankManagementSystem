package com.clients.account.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AccountUpdateRequestDTO(
        @Min(value = 1000000, message = "Customer ID must be a 7-digit number")
        @Max(value = 9999999, message = "Customer ID must be a 7-digit number")
        Integer customerId,

        @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be non-negative")
        BigDecimal balance,

        @Pattern(regexp = "salary|savings|investment", message = "Account type must be either salary, savings, or investment")
        String accountType,

        @Pattern(regexp = "active|inactive|closed", message = "Account status must be either active, inactive, or closed")
        String accountStatus
){
}

package com.clients.account.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AccountRequestDTO(
        @NotNull(message = "Customer ID cannot be null")
        @Min(value = 1000000, message = "Customer ID must be a 7-digit number")
        @Max(value = 9999999, message = "Customer ID must be a 7-digit number")
        Integer customerId,

        @NotNull(message = "Balance cannot be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be non-negative")
        BigDecimal balance,

        @NotBlank(message = "Account type cannot be blank")
        @Pattern(regexp = "salary|savings|investment", message = "Account type must be either salary, savings, or investment")
        String accountType,

        @NotBlank(message = "Account status cannot be blank")
        @Pattern(regexp = "active|inactive|closed", message = "Account status must be either active, inactive, or closed")
        String accountStatus
) {
}

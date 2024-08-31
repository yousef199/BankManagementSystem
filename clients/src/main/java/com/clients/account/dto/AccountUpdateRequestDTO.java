package com.clients.account.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AccountUpdateRequestDTO(
        @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be non-negative")
        BigDecimal balance,

        @Pattern(regexp = "salary|savings|investment", message = "Account type must be either salary, savings, or investment")
        String accountType,

        @Pattern(regexp = "active|inactive|closed", message = "Account status must be either active, inactive, or closed")
        String accountStatus
){
}

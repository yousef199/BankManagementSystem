package com.account;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record AccountRequestDTO(
        @NotBlank(message = "Customer ID cannot be blank")
        @Pattern(regexp = "\\d{7}", message = "Customer ID must be a 7-digit number")
        String customerId,

        @NotNull(message = "Initial balance cannot be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Initial balance must be non-negative")
        BigDecimal initialBalance,

        @NotBlank(message = "Account type cannot be blank")
        @Pattern(regexp = "salary|savings|investment", message = "Account type must be either salary, savings, or investment")
        String accountType
) {
}

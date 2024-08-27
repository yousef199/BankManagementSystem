package com.account;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record AccountResponseDTO(
        String accountId,
        String customerId,
        BigDecimal balance,
        String accountType,
        String status,
        String message
) {
}

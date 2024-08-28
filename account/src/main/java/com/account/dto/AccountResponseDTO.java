package com.account.dto;

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

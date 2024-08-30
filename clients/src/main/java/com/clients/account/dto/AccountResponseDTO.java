package com.clients.account.dto;

import java.math.BigDecimal;

public record AccountResponseDTO(
        int httpStatus,  // HTTP status code
        int accountId,
        int customerId,
        BigDecimal balance,
        String accountType,
        String accountStatus,
        String message
) {
}

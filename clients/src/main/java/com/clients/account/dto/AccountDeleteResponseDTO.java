package com.clients.account.dto;

public record AccountDeleteResponseDTO(
        int statusCode,
        int customerId,
        String message
) {
}

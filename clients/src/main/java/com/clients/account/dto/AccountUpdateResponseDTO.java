package com.clients.account.dto;

import java.util.Map;

public record AccountUpdateResponseDTO(
        int statusCode,
        int customerId,
        Map<String, Object> updatedFields,
        String message
) {
}

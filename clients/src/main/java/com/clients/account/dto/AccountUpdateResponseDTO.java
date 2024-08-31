package com.clients.account.dto;

import java.util.Map;

public record AccountUpdateResponseDTO(
        int statusCode,
        Map<String, Object> updatedFields,
        String message
) {
}

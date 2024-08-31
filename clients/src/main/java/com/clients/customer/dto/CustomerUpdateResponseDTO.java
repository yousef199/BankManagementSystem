package com.clients.customer.dto;

import java.util.Map;

public record CustomerUpdateResponseDTO (
        int httpStatus,
        int customerId,
        Map<String , Object> updatedFields,
        String message
){
}

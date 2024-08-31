package com.clients.customer.dto;

public record CustomerDeleteResponseDTO (
        int httpStatus,
        int customerId,
        String message
){
}

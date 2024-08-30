package com.clients.customer.dto;

public record CustomerResponseDTO(
        String customerId,
        String name,
        String legalId,
        String type,
        String address,
        String phoneNumber,
        String email,
        String status,
        String message
) {
}
package com.clients.customer.dto;

public record CustomerResponseDTO(
        int httpStatus,
        String customerId,
        String name,
        String legalId,
        String type,
        String address,
        String phoneNumber,
        String email,
        int numberOfAccounts,
        String customerStatus, // Added status
        String message
) {
}

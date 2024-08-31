package com.clients.customer.dto;

public record KafkaNewCustomerDTO (
        int customerId,
        String name,
        String legalId,
        String type,
        String address,
        String phoneNumber,
        String email,
        int numberOfAccounts,
        String customerStatus
        ){
}

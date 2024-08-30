package com.clients.account.dto;

public record KafkaNewAccountDTO(
        int accountId,
        int customerId,
        String accountType,
        String accountStatus
) {
}

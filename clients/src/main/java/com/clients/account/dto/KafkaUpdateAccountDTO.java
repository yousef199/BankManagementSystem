package com.clients.account.dto;

import java.util.Map;

public record KafkaUpdateAccountDTO (
        int accountId,
        int customerId,
        Map<String, Object> updatedFields
){
}

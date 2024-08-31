package com.clients.customer.dto;

import java.util.Map;

public record KafkaCustomerUpdateDTO(
        Integer customerId,
        Map<String, Object> updatedFields
) {
}

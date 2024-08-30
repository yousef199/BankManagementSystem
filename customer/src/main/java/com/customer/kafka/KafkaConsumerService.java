package com.customer.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = {"account.new"})
    public void newCustomerEvent(String message) {
        log.info("consumed account created event: {}", message);
    }


    @KafkaListener(topics = {"account.update"})
    public void updateCustomerEvent(String message) {
        log.info("consumed account updated event: {}", message);
    }


    @KafkaListener(topics = {"account.delete"})
    public void deleteCustomerEvent(String message) {
        log.info("consumed account deleted event: {}", message);
    }
}
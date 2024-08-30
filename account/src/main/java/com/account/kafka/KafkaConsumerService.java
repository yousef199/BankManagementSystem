package com.account.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = {"customer.new"})
    public void newCustomerEvent(String message) {
        log.info("consumed customer created event: {}", message);
    }


    @KafkaListener(topics = {"customer.update"})
    public void updateCustomerEvent(String message) {
        log.info("consumed customer updated event: {}", message);
    }


    @KafkaListener(topics = {"customer.delete"})
    public void deleteCustomerEvent(String message) {
        log.info("consumed customer deleted event: {}", message);
    }
}
package com.customer.kafka;

import com.clients.account.dto.KafkaDeleteAccountDTO;
import com.clients.account.dto.KafkaNewAccountDTO;
import com.clients.account.dto.KafkaUpdateAccountDTO;
import com.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final CustomerService customerService;

    @KafkaListener(topics = {"account.new"})
    public void newCustomerEvent(KafkaNewAccountDTO kafkaNewAccountDTO) {
        log.debug("consumed account created event: {}", kafkaNewAccountDTO);
        customerService.handleNewAccountEvent(kafkaNewAccountDTO);
    }


    @KafkaListener(topics = {"account.update"})
    public void updateCustomerEvent(KafkaUpdateAccountDTO kafkaUpdateAccountDTO) {
        log.debug("consumed account updated event: {}", kafkaUpdateAccountDTO);
    }


    @KafkaListener(topics = {"account.delete"})
    public void deleteCustomerEvent(KafkaDeleteAccountDTO kafkaDeleteAccountDTO) {
        log.debug("consumed account deleted event: {}", kafkaDeleteAccountDTO);
        customerService.handleDeleteAccountEvent(kafkaDeleteAccountDTO);
    }
}
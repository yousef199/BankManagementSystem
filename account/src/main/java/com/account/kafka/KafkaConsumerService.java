package com.account.kafka;

import com.account.service.AccountService;
import com.clients.customer.dto.KafkaCustomerDeleteDTO;
import com.clients.customer.dto.KafkaCustomerUpdateDTO;
import com.clients.customer.dto.KafkaNewCustomerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final AccountService accountService;

    @KafkaListener(topics = {"customer.new"})
    public void newCustomerEvent(KafkaNewCustomerDTO kafkaNewCustomerDTO) {
        log.debug("consumed customer created event: {}", kafkaNewCustomerDTO);
    }


    @KafkaListener(topics = {"customer.update"})
    public void updateCustomerEvent(KafkaCustomerUpdateDTO kafkaCustomerUpdateDTO) {
        log.debug("consumed customer updated event: {}", kafkaCustomerUpdateDTO);
        accountService.handleUpdateCustomerEvent(kafkaCustomerUpdateDTO);
    }


    @KafkaListener(topics = {"customer.delete"})
    public void deleteCustomerEvent(KafkaCustomerDeleteDTO kafkaCustomerDeleteDTO) {
        log.debug("consumed customer deleted event: {}", kafkaCustomerDeleteDTO);
        accountService.handleDeleteCustomerEvent(kafkaCustomerDeleteDTO);
    }
}
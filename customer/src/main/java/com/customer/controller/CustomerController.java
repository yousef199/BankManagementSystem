package com.customer.controller;

import com.clients.customer.dto.*;
import com.clients.dto.GeneralResponseDTO;
import com.common.enums.TopicNames;
import com.customer.kafka.KafkaProducerService;
import com.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@Slf4j
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/registerCustomer")
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO createdCustomerDTO = customerService.createCustomer(customerRequestDTO);
        KafkaNewCustomerDTO kafkaNewCustomerDTO = new KafkaNewCustomerDTO(createdCustomerDTO.customerId() , createdCustomerDTO.name(),
                createdCustomerDTO.legalId() , createdCustomerDTO.type() , createdCustomerDTO.address() , createdCustomerDTO.phoneNumber(),
                createdCustomerDTO.email() , createdCustomerDTO.numberOfAccounts() , createdCustomerDTO.customerStatus());
        kafkaProducerService.sendMessage(TopicNames.CUSTOMER_NEW.getTopicName(), kafkaNewCustomerDTO);
        return new ResponseEntity<>(createdCustomerDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDTO> getCustomer(@PathVariable int customerId) {
        CustomerResponseDTO customerResponseDTO = customerService.getCustomer(customerId);
        return ResponseEntity.ok(customerResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerUpdateResponseDTO> updateCustomer(@PathVariable int customerId, @Valid @RequestBody CustomerUpdateRequestDTO customerUpdateRequestDTO) {
        CustomerUpdateResponseDTO updateResponseDTO = customerService.updateCustomer(customerId, customerUpdateRequestDTO);
        KafkaCustomerUpdateDTO kafkaCustomerUpdateDTO = new KafkaCustomerUpdateDTO(customerId , updateResponseDTO.updatedFields());
        kafkaProducerService.sendMessage(TopicNames.CUSTOMER_UPDATE.getTopicName(), kafkaCustomerUpdateDTO);
        return ResponseEntity.ok(updateResponseDTO);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<CustomerDeleteResponseDTO> deleteCustomer(@PathVariable int customerId) {
        CustomerDeleteResponseDTO generalResponseDTO = customerService.deleteCustomer(customerId);
        KafkaCustomerDeleteDTO kafkaCustomerDeleteDTO = new KafkaCustomerDeleteDTO(customerId);
        kafkaProducerService.sendMessage(TopicNames.CUSTOMER_DELETE.getTopicName(), kafkaCustomerDeleteDTO);
        return ResponseEntity.ok(generalResponseDTO);
    }
}
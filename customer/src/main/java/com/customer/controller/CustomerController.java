package com.customer.controller;

import com.clients.customer.dto.CustomerRequestDTO;
import com.clients.customer.dto.CustomerResponseDTO;
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
        CustomerResponseDTO createdCustomer = customerService.createCustomer(customerRequestDTO);
        kafkaProducerService.sendMessage(TopicNames.CUSTOMER_NEW.getTopicName(), "new customer created");
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDTO> getCustomer(@PathVariable String customerId) {
        CustomerResponseDTO customerResponseDTO = customerService.getCustomer(customerId);
        return ResponseEntity.ok(customerResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<GeneralResponseDTO> updateCustomer(@PathVariable String customerId, @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        GeneralResponseDTO generalResponseDTO = customerService.updateCustomer(customerId, customerRequestDTO);
        return ResponseEntity.ok(generalResponseDTO);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<GeneralResponseDTO> deleteCustomer(@PathVariable String customerId) {
        GeneralResponseDTO generalResponseDTO = customerService.deleteCustomer(customerId);
        return ResponseEntity.ok(generalResponseDTO);
    }
}
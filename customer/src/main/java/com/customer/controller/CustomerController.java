package com.customer.controller;

import com.clients.customer.dto.*;
import com.clients.dto.ErrorResponseDTO;
import com.common.enums.TopicNames;
import com.customer.kafka.KafkaProducerService;
import com.customer.service.CustomerService;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YQadous
 * Controller class for managing customers.
 */
@RestController
@RequestMapping("/api/v1/customers")
@Slf4j
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final KafkaProducerService kafkaProducerService;

    /**
     * Registers a new customer.
     *
     * @param customerRequestDTO The customer data to be registered.
     * @return A {@link ResponseEntity} containing the details of the created customer and HTTP status code 201.
     */
    @PostMapping(value = "/registerCustomer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registers a new customer", responses = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully", content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        log.debug("Received request to register customer: {}", customerRequestDTO);

        CustomerResponseDTO createdCustomerDTO = customerService.createCustomer(customerRequestDTO);
        log.info("Customer created successfully with ID: {}", createdCustomerDTO.customerId());

        KafkaNewCustomerDTO kafkaNewCustomerDTO = new KafkaNewCustomerDTO(
                createdCustomerDTO.customerId(),
                createdCustomerDTO.name(),
                createdCustomerDTO.legalId(),
                createdCustomerDTO.type(),
                createdCustomerDTO.address(),
                createdCustomerDTO.phoneNumber(),
                createdCustomerDTO.email(),
                createdCustomerDTO.numberOfAccounts(),
                createdCustomerDTO.customerStatus()
        );

        log.debug("Sending Kafka message for new customer: {}", kafkaNewCustomerDTO);
        kafkaProducerService.sendMessage(TopicNames.CUSTOMER_NEW.getTopicName(), kafkaNewCustomerDTO);

        return new ResponseEntity<>(createdCustomerDTO, HttpStatus.CREATED);
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param customerId The ID of the customer to retrieve.
     * @return A {@link ResponseEntity} containing the customer's details and HTTP status code 200.
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDTO> getCustomer(@PathVariable int customerId) {
        log.debug("Received request to get customer with ID: {}", customerId);

        CustomerResponseDTO customerResponseDTO = customerService.getCustomer(customerId);
        log.info("Retrieved customer with ID: {}", customerId);

        return ResponseEntity.ok(customerResponseDTO);
    }

    /**
     * Retrieves all customers.
     *
     * @return A {@link ResponseEntity} containing a list of all customers and HTTP status code 200.
     */
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        log.debug("Received request to get all customers");

        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        log.info("Retrieved {} customers", customers.size());

        return ResponseEntity.ok(customers);
    }

    /**
     * Updates an existing customer.
     *
     * @param customerId The ID of the customer to update.
     * @param customerUpdateRequestDTO The data to update the customer with.
     * @return A {@link ResponseEntity} containing the response DTO with updated fields and HTTP status code 200.
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerUpdateResponseDTO> updateCustomer(
            @PathVariable int customerId,
            @Valid @RequestBody CustomerUpdateRequestDTO customerUpdateRequestDTO) {

        log.debug("Received request to update customer with ID: {} and data: {}", customerId, customerUpdateRequestDTO);

        CustomerUpdateResponseDTO updateResponseDTO = customerService.updateCustomer(customerId, customerUpdateRequestDTO);
        log.info("Customer with ID: {} updated successfully", customerId);

        KafkaCustomerUpdateDTO kafkaCustomerUpdateDTO = new KafkaCustomerUpdateDTO(customerId, updateResponseDTO.updatedFields());
        log.debug("Sending Kafka message for updated customer: {}", kafkaCustomerUpdateDTO);
        kafkaProducerService.sendMessage(TopicNames.CUSTOMER_UPDATE.getTopicName(), kafkaCustomerUpdateDTO);

        return ResponseEntity.ok(updateResponseDTO);
    }

    /**
     * Deletes a customer by their ID.
     *
     * @param customerId The ID of the customer to delete.
     * @return A {@link ResponseEntity} containing the response DTO confirming the deletion and HTTP status code 200.
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<CustomerDeleteResponseDTO> deleteCustomer(@PathVariable int customerId) {
        log.debug("Received request to delete customer with ID: {}", customerId);

        CustomerDeleteResponseDTO generalResponseDTO = customerService.deleteCustomer(customerId);
        log.info("Customer with ID: {} deleted successfully", customerId);

        KafkaCustomerDeleteDTO kafkaCustomerDeleteDTO = new KafkaCustomerDeleteDTO(customerId);
        log.debug("Sending Kafka message for deleted customer: {}", kafkaCustomerDeleteDTO);
        kafkaProducerService.sendMessage(TopicNames.CUSTOMER_DELETE.getTopicName(), kafkaCustomerDeleteDTO);

        return ResponseEntity.ok(generalResponseDTO);
    }
}

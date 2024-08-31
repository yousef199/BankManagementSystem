package com.customer.controller;

import com.clients.customer.dto.*;
import com.common.enums.CustomerStatus;
import com.common.enums.CustomerTypes;
import com.common.enums.TopicNames;
import com.customer.kafka.KafkaProducerService;
import com.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCustomerSuccessfully() throws Exception {
        // Given
        CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO(
                "John Doe",
                "123456789",
                CustomerTypes.INVESTMENT.getType(),
                "123 Main St",
                "+962780167888",
                "john.doe@example.com",
                CustomerStatus.ACTIVE.getStatus()
        );

        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO(
                HttpStatus.CREATED.value(),
                1000000,
                "John Doe",
                "123456789",
                CustomerTypes.INVESTMENT.getType(),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                0,
                CustomerStatus.ACTIVE.getStatus(),
                "Customer with id: 1000000 created successfully"
        );

        when(customerService.createCustomer(any(CustomerRequestDTO.class))).thenReturn(customerResponseDTO);

        // When
        mockMvc.perform(post("/api/v1/customers/registerCustomer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId", is(1000000)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.customerStatus", is(CustomerStatus.ACTIVE.getStatus())))
                .andExpect(jsonPath("$.type", is(CustomerTypes.INVESTMENT.getType())))
                .andExpect(jsonPath("$.message", is("Customer with id: 1000000 created successfully")));

        // Then
        verify(customerService, times(1)).createCustomer(any(CustomerRequestDTO.class));
        verify(kafkaProducerService, times(1)).sendMessage(eq(TopicNames.CUSTOMER_NEW.getTopicName()), any(KafkaNewCustomerDTO.class));
    }

    @Test
    public void shouldReturnCustomerSuccessfully() throws Exception {
        // Given
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO(
                HttpStatus.CREATED.value(),
                1000000,
                "John Doe",
                "123456789",
                CustomerTypes.INVESTMENT.getType(),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                0,
                CustomerStatus.ACTIVE.getStatus(),
                "Customer with id: 1000000 created successfully"
        );

        when(customerService.getCustomer(1000000)).thenReturn(customerResponseDTO);

        // When
        mockMvc.perform(get("/api/v1/customers/1000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(1000000)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.customerStatus", is(CustomerStatus.ACTIVE.getStatus())))
                .andExpect(jsonPath("$.type", is(CustomerTypes.INVESTMENT.getType())))
                .andExpect(jsonPath("$.message", is("Customer with id: 1000000 created successfully")));

        // Then
        verify(customerService, times(1)).getCustomer(1000000);
    }

    @Test
    public void shouldReturnAllCustomersSuccessfully() throws Exception {
        // Given
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO(
                HttpStatus.CREATED.value(),
                1000000,
                "John Doe",
                "123456789",
                CustomerTypes.INVESTMENT.getType(),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                0,
                CustomerStatus.ACTIVE.getStatus(),
                "Customer with id: 1000000 created successfully"
        );

        when(customerService.getAllCustomers()).thenReturn(List.of(customerResponseDTO));

        // When
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId", is(1000000)))
                .andExpect(jsonPath("$[0].name", is("John Doe")));

        // Then
        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    public void shouldUpdateCustomerSuccessfully() throws Exception {
        // Given
        CustomerUpdateRequestDTO customerUpdateRequestDTO = new CustomerUpdateRequestDTO(
                "Jane Doe",
                "987654321",
                CustomerTypes.INVESTMENT.getType(),
                "456 Elm St",
                "+962780167888",
                "jane.doe@example.com",
                CustomerStatus.INACTIVE.getStatus()
        );

        CustomerUpdateResponseDTO customerUpdateResponseDTO = new CustomerUpdateResponseDTO(
                HttpStatus.OK.value(),
                1000000,
                Map.of("name", "Jane Doe", "legalId", "987654321", "type", "Business", "address", "456 Elm St", "phoneNumber", "555-5678", "email", "jane.doe@example.com", "customerStatus", CustomerStatus.INACTIVE),
                "Customer with id: 1000000 updated successfully"
        );

        when(customerService.updateCustomer(eq(1000000), any(CustomerUpdateRequestDTO.class))).thenReturn(customerUpdateResponseDTO);

        // When
        mockMvc.perform(put("/api/v1/customers/1000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerUpdateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(1000000)))
                .andExpect(jsonPath("$.updatedFields.name", is("Jane Doe")));

        // Then
        verify(customerService, times(1)).updateCustomer(eq(1000000), any(CustomerUpdateRequestDTO.class));
        verify(kafkaProducerService, times(1)).sendMessage(eq(TopicNames.CUSTOMER_UPDATE.getTopicName()), any(KafkaCustomerUpdateDTO.class));
    }

    @Test
    public void shouldDeleteCustomerSuccessfully() throws Exception {
        // Given
        CustomerDeleteResponseDTO customerDeleteResponseDTO = new CustomerDeleteResponseDTO(
                HttpStatus.OK.value(),
                1000000,
                "Customer with id: 1000000 deleted successfully"
        );

        when(customerService.deleteCustomer(1000000)).thenReturn(customerDeleteResponseDTO);

        // When
        mockMvc.perform(delete("/api/v1/customers/1000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(1000000)))
                .andExpect(jsonPath("$.message", is("Customer with id: 1000000 deleted successfully")));

        // Then
        verify(customerService, times(1)).deleteCustomer(1000000);
        verify(kafkaProducerService, times(1)).sendMessage(eq(TopicNames.CUSTOMER_DELETE.getTopicName()), any(KafkaCustomerDeleteDTO.class));
    }
}

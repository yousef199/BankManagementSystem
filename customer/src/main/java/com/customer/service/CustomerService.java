package com.customer.service;

import com.clients.account.AccountClient;
import com.clients.customer.dto.*;
import com.clients.dto.GeneralResponseDTO;
import com.common.enums.CustomerStatus;
import com.customer.entity.Customer;
import com.customer.exception.CustomerNotFoundException;
import com.customer.exception.InvalidCustomerDeleteReqeustException;
import com.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AccountClient accountClient;

    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        Customer customer = new Customer();
        customer.setName(customerRequestDTO.name());
        customer.setLegalId(customerRequestDTO.legalId());
        customer.setType(customerRequestDTO.type());
        customer.setAddress(customerRequestDTO.address());
        customer.setPhoneNumber(customerRequestDTO.phoneNumber());
        customer.setEmail(customerRequestDTO.email());
        customer.setCustomerStatus(customerRequestDTO.customerStatus()); // Default status for new customers
        customer.setNumberOfAccounts(0); // Default number of accounts

        // Save customer in the repository
        Customer savedCustomer = customerRepository.save(customer);

        // Return response DTO
        return new CustomerResponseDTO(
                HttpStatus.CREATED.value(),  // HTTP status code
                savedCustomer.getCustomerId(),
                savedCustomer.getName(),
                savedCustomer.getLegalId(),
                savedCustomer.getType(),
                savedCustomer.getAddress(),
                savedCustomer.getPhoneNumber(),
                savedCustomer.getEmail(),
                savedCustomer.getNumberOfAccounts(),
                savedCustomer.getCustomerStatus(),
                "Customer created successfully"
        );
    }

    public CustomerResponseDTO getCustomer(int customerId) {
        // Fetch the customer from the repository
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id: " + customerId + " not found"));

        // Return response DTO
        return new CustomerResponseDTO(
                HttpStatus.OK.value(),  // HTTP status code
                customer.getCustomerId(),
                customer.getName(),
                customer.getLegalId(),
                customer.getType(),
                customer.getAddress(),
                customer.getPhoneNumber(),
                customer.getEmail(),
                customer.getNumberOfAccounts(),
                customer.getCustomerStatus(),
                "Customer retrieved successfully"
        );
    }

    public List<CustomerResponseDTO> getAllCustomers() {
        // Fetch all customers from the repository
        List<Customer> customers = customerRepository.findAll();

        // Convert to DTOs
        return customers.stream().map(customer -> new CustomerResponseDTO(
                HttpStatus.OK.value(),  // HTTP status code
                customer.getCustomerId(),
                customer.getName(),
                customer.getLegalId(),
                customer.getType(),
                customer.getAddress(),
                customer.getPhoneNumber(),
                customer.getEmail(),
                customer.getNumberOfAccounts(),
                customer.getCustomerStatus(),
                "Customer retrieved successfully"
        )).toList();
    }

    public CustomerUpdateResponseDTO updateCustomer(int customerId, CustomerUpdateRequestDTO customerRequestDTO) {
        // Fetch the customer to update
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id: " + customerId + " not found"));

        // Map to track updated fields
        Map<String, Object> updatedFields = new HashMap<>();

        // Check for non-null changes and update customer details
        if (customerRequestDTO.name() != null && !Objects.equals(customer.getName(), customerRequestDTO.name())) {
            customer.setName(customerRequestDTO.name());
            updatedFields.put("name", customerRequestDTO.name());
        }
        if (customerRequestDTO.legalId() != null && !Objects.equals(customer.getLegalId(), customerRequestDTO.legalId())) {
            customer.setLegalId(customerRequestDTO.legalId());
            updatedFields.put("legalId", customerRequestDTO.legalId());
        }
        if (customerRequestDTO.type() != null && !Objects.equals(customer.getType(), customerRequestDTO.type())) {
            customer.setType(customerRequestDTO.type());
            updatedFields.put("type", customerRequestDTO.type());
        }
        if (customerRequestDTO.address() != null && !Objects.equals(customer.getAddress(), customerRequestDTO.address())) {
            customer.setAddress(customerRequestDTO.address());
            updatedFields.put("address", customerRequestDTO.address());
        }
        if (customerRequestDTO.phoneNumber() != null && !Objects.equals(customer.getPhoneNumber(), customerRequestDTO.phoneNumber())) {
            customer.setPhoneNumber(customerRequestDTO.phoneNumber());
            updatedFields.put("phoneNumber", customerRequestDTO.phoneNumber());
        }
        if (customerRequestDTO.email() != null && !Objects.equals(customer.getEmail(), customerRequestDTO.email())) {
            customer.setEmail(customerRequestDTO.email());
            updatedFields.put("email", customerRequestDTO.email());
        }
        if (customerRequestDTO.customerStatus() != null && !Objects.equals(customer.getCustomerStatus(), customerRequestDTO.customerStatus())) {
            customer.setCustomerStatus(customerRequestDTO.customerStatus());
            updatedFields.put("customerStatus", customerRequestDTO.customerStatus());
        }

        // Save the updated customer
        customerRepository.save(customer);

        // Return response DTO
        return new CustomerUpdateResponseDTO(
                HttpStatus.OK.value(),
                customerId,
                updatedFields,
                "Customer with id: " + customerId + " updated successfully"
        );
    }

    public CustomerDeleteResponseDTO deleteCustomer(int customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id: " + customerId + " not found"));

        // Check if customer has any accounts
        if (customer.getNumberOfAccounts() > 0) {
            throw new InvalidCustomerDeleteReqeustException("Customer with id: " + customerId + " has accounts and cannot be deleted");
        }
        // Delete the customer
        customerRepository.delete(customer);

        // Return response DTO
        return new CustomerDeleteResponseDTO(
                HttpStatus.OK.value(),
                customerId,
                "Customer with id: " + customerId + " deleted successfully"
        );
    }
}
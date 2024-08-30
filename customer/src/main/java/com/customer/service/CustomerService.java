package com.customer.service;

import com.clients.account.AccountClient;
import com.clients.customer.dto.CustomerRequestDTO;
import com.clients.customer.dto.CustomerResponseDTO;
import com.clients.dto.GeneralResponseDTO;
import com.common.enums.CustomerStatus;
import com.customer.entity.Customer;
import com.customer.exception.CustomerNotFoundException;
import com.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AccountClient accountClient;

    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        // Create a new Customer entity from DTO
        Customer customer = new Customer();
        customer.setName(customerRequestDTO.name());
        customer.setLegalId(customerRequestDTO.legalId());
        customer.setType(customerRequestDTO.type());
        customer.setAddress(customerRequestDTO.address());
        customer.setPhoneNumber(customerRequestDTO.phoneNumber());
        customer.setEmail(customerRequestDTO.email());
        customer.setCustomerStatus(CustomerStatus.ACTIVE.getStatus()); // Default status for new customers

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

    public CustomerResponseDTO getCustomer(String customerId) {
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

    public GeneralResponseDTO updateCustomer(String customerId, CustomerRequestDTO customerRequestDTO) {
        // Fetch the customer to update
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id: " + customerId + " not found"));

        // Update customer details
        customer.setName(customerRequestDTO.name());
        customer.setLegalId(customerRequestDTO.legalId());
        customer.setType(customerRequestDTO.type());
        customer.setAddress(customerRequestDTO.address());
        customer.setPhoneNumber(customerRequestDTO.phoneNumber());
        customer.setEmail(customerRequestDTO.email());
        customer.setCustomerStatus(customerRequestDTO.customerStatus()); // Update status

        // Save the updated customer
        customerRepository.save(customer);

        // Return response DTO
        return new GeneralResponseDTO(
                HttpStatus.OK.value(),
                "Customer with id: " + customerId + " updated successfully"
        );
    }

    public GeneralResponseDTO deleteCustomer(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id: " + customerId + " not found"));

        // Delete the customer
        customerRepository.delete(customer);

        // Return response DTO
        return new GeneralResponseDTO(
                HttpStatus.OK.value(),
                "Customer with id: " + customerId + " deleted successfully"
        );
    }
}
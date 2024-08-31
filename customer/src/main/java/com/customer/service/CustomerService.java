package com.customer.service;

import com.clients.account.AccountClient;
import com.clients.customer.dto.*;
import com.customer.entity.Customer;
import com.customer.exception.CustomerNotFoundException;
import com.customer.exception.InvalidCustomerDeleteReqeustException;
import com.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author YQadous
 * Service class for managing customers.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountClient accountClient;

    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        log.debug("Creating customer with data: {}", customerRequestDTO);

        Customer customer = mapToCustomer(customerRequestDTO);
        Customer savedCustomer = customerRepository.save(customer);

        log.info("Customer created successfully with ID: {}", savedCustomer.getCustomerId());

        return mapToCustomerResponseDTO(savedCustomer, HttpStatus.CREATED.value(), "Customer created successfully");
    }

    public CustomerResponseDTO getCustomer(int customerId) {
        log.debug("Fetching customer with ID: {}", customerId);

        Customer customer = findCustomerById(customerId);

        log.info("Customer retrieved successfully with ID: {}", customerId);
        return mapToCustomerResponseDTO(customer, HttpStatus.OK.value(), "Customer retrieved successfully");
    }

    public List<CustomerResponseDTO> getAllCustomers() {
        log.debug("Fetching all customers");

        List<Customer> customers = customerRepository.findAll();
        log.info("Retrieved {} customers", customers.size());

        return customers.stream()
                .map(customer -> mapToCustomerResponseDTO(customer, HttpStatus.OK.value(), "Customer retrieved successfully"))
                .toList();
    }

    public CustomerUpdateResponseDTO updateCustomer(int customerId, CustomerUpdateRequestDTO customerRequestDTO) {
        log.debug("Updating customer with ID: {} with data: {}", customerId, customerRequestDTO);

        Customer customer = findCustomerById(customerId);
        Map<String, Object> updatedFields = updateCustomerFields(customer, customerRequestDTO);
        customerRepository.save(customer);

        log.info("Customer with ID: {} updated successfully. Updated fields: {}", customerId, updatedFields);

        return new CustomerUpdateResponseDTO(HttpStatus.OK.value(), customerId, updatedFields, "Customer with id: " + customerId + " updated successfully");
    }

    public CustomerDeleteResponseDTO deleteCustomer(int customerId) {
        log.debug("Attempting to delete customer with ID: {}", customerId);

        Customer customer = findCustomerById(customerId);
        try {
            validateCustomerDeletion(customer);
            customerRepository.delete(customer);

            log.info("Customer with ID: {} deleted successfully", customerId);
            return new CustomerDeleteResponseDTO(HttpStatus.OK.value(), customerId, "Customer with id: " + customerId + " deleted successfully");
        } catch (InvalidCustomerDeleteReqeustException e) {
            log.warn("Customer with ID: {} cannot be deleted: {}", customerId, e.getMessage());
            throw e; // Re-throw to propagate the error
        }
    }

    private Customer findCustomerById(int customerId) {
        log.debug("Finding customer by ID: {}", customerId);

        return customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer with ID: {} not found", customerId);
                    return new CustomerNotFoundException("Customer with id: " + customerId + " not found");
                });
    }

    private Customer mapToCustomer(CustomerRequestDTO customerRequestDTO) {
        // No logging needed here as this is a simple mapping method
        Customer customer = new Customer();
        customer.setName(customerRequestDTO.name());
        customer.setLegalId(customerRequestDTO.legalId());
        customer.setType(customerRequestDTO.type());
        customer.setAddress(customerRequestDTO.address());
        customer.setPhoneNumber(customerRequestDTO.phoneNumber());
        customer.setEmail(customerRequestDTO.email());
        customer.setCustomerStatus(customerRequestDTO.customerStatus());
        customer.setNumberOfAccounts(0); // Default number of accounts
        return customer;
    }

    private CustomerResponseDTO mapToCustomerResponseDTO(Customer customer, int statusCode, String message) {
        // No logging needed here as this is a simple mapping method
        return new CustomerResponseDTO(
                statusCode,
                customer.getCustomerId(),
                customer.getName(),
                customer.getLegalId(),
                customer.getType(),
                customer.getAddress(),
                customer.getPhoneNumber(),
                customer.getEmail(),
                customer.getNumberOfAccounts(),
                customer.getCustomerStatus(),
                message
        );
    }

    private Map<String, Object> updateCustomerFields(Customer customer, CustomerUpdateRequestDTO customerRequestDTO) {
        log.debug("Updating fields of customer with ID: {}", customer.getCustomerId());

        Map<String, Object> updatedFields = new HashMap<>();

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

        log.debug("Updated fields for customer with ID: {}: {}", customer.getCustomerId(), updatedFields);
        return updatedFields;
    }

    private void validateCustomerDeletion(Customer customer) {
        log.debug("Validating if customer with ID: {} can be deleted", customer.getCustomerId());

        if (customer.getNumberOfAccounts() > 0) {
            log.warn("Customer with ID: {} has accounts and cannot be deleted", customer.getCustomerId());
            throw new InvalidCustomerDeleteReqeustException("Customer with id: " + customer.getCustomerId() + " has accounts and cannot be deleted");
        }
    }
}


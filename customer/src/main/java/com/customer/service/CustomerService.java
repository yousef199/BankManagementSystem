package com.customer.service;

import com.clients.account.AccountClient;
import com.clients.customer.dto.*;
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

/**
 * Service class for managing customers.
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountClient accountClient;

    /**
     * Creates a new customer and saves it in the repository.
     *
     * @param customerRequestDTO The customer data for creating a new customer.
     * @return The response DTO containing the created customer's details.
     */
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        Customer customer = mapToCustomer(customerRequestDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return mapToCustomerResponseDTO(savedCustomer, HttpStatus.CREATED.value(), "Customer created successfully");
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param customerId The ID of the customer to retrieve.
     * @return The response DTO containing the customer's details.
     */
    public CustomerResponseDTO getCustomer(int customerId) {
        Customer customer = findCustomerById(customerId);
        return mapToCustomerResponseDTO(customer, HttpStatus.OK.value(), "Customer retrieved successfully");
    }

    /**
     * Retrieves all customers.
     *
     * @return A list of response DTOs containing the details of all customers.
     */
    public List<CustomerResponseDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customer -> mapToCustomerResponseDTO(customer, HttpStatus.OK.value(), "Customer retrieved successfully"))
                .toList();
    }

    /**
     * Updates an existing customer with the provided data.
     *
     * @param customerId          The ID of the customer to update.
     * @param customerRequestDTO  The data to update the customer with.
     * @return The response DTO containing the updated fields of the customer.
     */
    public CustomerUpdateResponseDTO updateCustomer(int customerId, CustomerUpdateRequestDTO customerRequestDTO) {
        Customer customer = findCustomerById(customerId);
        Map<String, Object> updatedFields = updateCustomerFields(customer, customerRequestDTO);
        customerRepository.save(customer);
        return new CustomerUpdateResponseDTO(HttpStatus.OK.value(), customerId, updatedFields, "Customer with id: " + customerId + " updated successfully");
    }

    /**
     * Deletes a customer by their ID.
     *
     * @param customerId The ID of the customer to delete.
     * @return The response DTO confirming the deletion.
     */
    public CustomerDeleteResponseDTO deleteCustomer(int customerId) {
        Customer customer = findCustomerById(customerId);
        validateCustomerDeletion(customer);
        customerRepository.delete(customer);
        return new CustomerDeleteResponseDTO(HttpStatus.OK.value(), customerId, "Customer with id: " + customerId + " deleted successfully");
    }

    /**
     * Finds a customer by their ID.
     *
     * @param customerId The ID of the customer to find.
     * @return The found customer.
     * @throws CustomerNotFoundException if the customer is not found.
     */
    private Customer findCustomerById(int customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id: " + customerId + " not found"));
    }

    /**
     * Maps a CustomerRequestDTO to a Customer entity.
     *
     * @param customerRequestDTO The DTO to map.
     * @return The mapped Customer entity.
     */
    private Customer mapToCustomer(CustomerRequestDTO customerRequestDTO) {
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

    /**
     * Maps a Customer entity to a CustomerResponseDTO.
     *
     * @param customer The customer entity to map.
     * @param statusCode The HTTP status code for the response.
     * @param message The message to include in the response.
     * @return The mapped CustomerResponseDTO.
     */
    private CustomerResponseDTO mapToCustomerResponseDTO(Customer customer, int statusCode, String message) {
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

    /**
     * Updates the fields of a Customer entity based on the provided DTO.
     *
     * @param customer The customer to update.
     * @param customerRequestDTO The DTO containing the new data.
     * @return A map of updated fields.
     */
    private Map<String, Object> updateCustomerFields(Customer customer, CustomerUpdateRequestDTO customerRequestDTO) {
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

        return updatedFields;
    }

    /**
     * Validates if a customer can be deleted.
     *
     * @param customer The customer to validate.
     * @throws InvalidCustomerDeleteReqeustException if the customer cannot be deleted.
     */
    private void validateCustomerDeletion(Customer customer) {
        if (customer.getNumberOfAccounts() > 0) {
            throw new InvalidCustomerDeleteReqeustException("Customer with id: " + customer.getCustomerId() + " has accounts and cannot be deleted");
        }
    }
}

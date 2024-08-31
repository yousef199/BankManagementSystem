package com.customer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.clients.account.AccountClient;
import com.clients.customer.dto.*;
import com.common.enums.CustomerStatus;
import com.customer.entity.Customer;
import com.customer.exception.CustomerNotFoundException;
import com.customer.exception.InvalidCustomerDeleteReqeustException;
import com.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldCreateCustomerSuccessfully() {
        // Arrange
        Customer customer = new Customer();
        customer.setCustomerId(1000000);
        customer.setName("John Doe");
        customer.setLegalId("123456789");
        customer.setType("Individual");
        customer.setAddress("123 Main St");
        customer.setPhoneNumber("555-1234");
        customer.setEmail("john.doe@example.com");
        customer.setCustomerStatus(CustomerStatus.ACTIVE.getStatus());
        customer.setNumberOfAccounts(0);

        CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO(
                "John Doe",
                "123456789",
                "Individual",
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                CustomerStatus.ACTIVE.getStatus()
        );

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        CustomerResponseDTO response = customerService.createCustomer(customerRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED.value(), response.httpStatus());
        assertEquals(customer.getCustomerId(), response.customerId());
        assertEquals(customer.getName(), response.name());
        assertEquals(customer.getLegalId(), response.legalId());
        assertEquals("Customer created successfully", response.message());

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void shouldReturnCustomerSuccessfully() {
        // Arrange
        Customer customer = new Customer();
        customer.setCustomerId(1000000);
        customer.setName("John Doe");
        customer.setLegalId("123456789");
        customer.setType("Individual");
        customer.setAddress("123 Main St");
        customer.setPhoneNumber("555-1234");
        customer.setEmail("john.doe@example.com");
        customer.setCustomerStatus(CustomerStatus.ACTIVE.getStatus());
        customer.setNumberOfAccounts(0);

        when(customerRepository.findById(1000000)).thenReturn(Optional.of(customer));

        // Act
        CustomerResponseDTO response = customerService.getCustomer(1000000);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.httpStatus());
        assertEquals(customer.getCustomerId(), response.customerId());
        assertEquals("Customer retrieved successfully", response.message());

        verify(customerRepository, times(1)).findById(1000000);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Arrange
        when(customerRepository.findById(1000000)).thenReturn(Optional.empty());

        // Act & Assert
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomer(1000000)
        );

        assertEquals("Customer with id: 1000000 not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(1000000);
    }

    @Test
    void shouldReturnAllCustomersSuccessfully() {
        // Arrange
        Customer customer = new Customer();
        customer.setCustomerId(1000000);
        customer.setName("John Doe");
        customer.setLegalId("123456789");
        customer.setType("Individual");
        customer.setAddress("123 Main St");
        customer.setPhoneNumber("555-1234");
        customer.setEmail("john.doe@example.com");
        customer.setCustomerStatus(CustomerStatus.ACTIVE.getStatus());
        customer.setNumberOfAccounts(0);

        List<Customer> customers = Collections.singletonList(customer);
        when(customerRepository.findAll()).thenReturn(customers);

        // Act
        List<CustomerResponseDTO> responses = customerService.getAllCustomers();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(customer.getCustomerId(), responses.get(0).customerId());
        assertEquals(HttpStatus.OK.value(), responses.get(0).httpStatus());

        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {
        // Arrange
        Customer customer = new Customer();
        customer.setCustomerId(1000000);
        customer.setName("John Doe");
        customer.setLegalId("123456789");
        customer.setType("Individual");
        customer.setAddress("123 Main St");
        customer.setPhoneNumber("555-1234");
        customer.setEmail("john.doe@example.com");
        customer.setCustomerStatus(CustomerStatus.ACTIVE.getStatus());
        customer.setNumberOfAccounts(0);

        CustomerUpdateRequestDTO customerUpdateRequestDTO = new CustomerUpdateRequestDTO(
                "Jane Doe",
                "987654321",
                "Business",
                "456 Elm St",
                "555-5678",
                "jane.doe@example.com",
                CustomerStatus.INACTIVE.getStatus()
        );

        when(customerRepository.findById(1000000)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        CustomerUpdateResponseDTO response = customerService.updateCustomer(1000000, customerUpdateRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.httpStatus());
        assertEquals(1000000, response.customerId());
        assertEquals(CustomerStatus.INACTIVE.getStatus() , response.updatedFields().get("customerStatus"));
        assertTrue(response.updatedFields().containsKey("name"));
        assertEquals("Customer with id: 1000000 updated successfully", response.message());

        verify(customerRepository, times(1)).findById(1000000);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentCustomer() {
        // Arrange
        CustomerUpdateRequestDTO customerUpdateRequestDTO = new CustomerUpdateRequestDTO(
                "Jane Doe",
                "987654321",
                "Business",
                "456 Elm St",
                "555-5678",
                "jane.doe@example.com",
                CustomerStatus.INACTIVE.getStatus()
        );

        when(customerRepository.findById(1000000)).thenReturn(Optional.empty());

        // Act & Assert
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.updateCustomer(1000000, customerUpdateRequestDTO)
        );

        assertEquals("Customer with id: 1000000 not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(1000000);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldDeleteCustomerSuccessfully() {
        // Arrange
        Customer customer = new Customer();
        customer.setCustomerId(1000000);
        customer.setName("John Doe");
        customer.setLegalId("123456789");
        customer.setType("Individual");
        customer.setAddress("123 Main St");
        customer.setPhoneNumber("555-1234");
        customer.setEmail("john.doe@example.com");
        customer.setCustomerStatus(CustomerStatus.ACTIVE.getStatus());
        customer.setNumberOfAccounts(0);

        when(customerRepository.findById(1000000)).thenReturn(Optional.of(customer));

        // Act
        CustomerDeleteResponseDTO response = customerService.deleteCustomer(1000000);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.httpStatus());
        assertEquals(1000000, response.customerId());
        assertEquals("Customer with id: 1000000 deleted successfully", response.message());

        verify(customerRepository, times(1)).findById(1000000);
        verify(customerRepository, times(1)).delete(customer);
    }

    @Test
    void shouldThrowExceptionWhenDeletingCustomerWithAccounts() {
        // Arrange
        Customer customer = new Customer();
        customer.setCustomerId(1000000);
        customer.setName("John Doe");
        customer.setLegalId("123456789");
        customer.setType("Individual");
        customer.setAddress("123 Main St");
        customer.setPhoneNumber("555-1234");
        customer.setEmail("john.doe@example.com");
        customer.setCustomerStatus(CustomerStatus.ACTIVE.getStatus());
        customer.setNumberOfAccounts(1);

        when(customerRepository.findById(1000000)).thenReturn(Optional.of(customer));

        // Act & Assert
        InvalidCustomerDeleteReqeustException exception = assertThrows(
                InvalidCustomerDeleteReqeustException.class,
                () -> customerService.deleteCustomer(1000000)
        );

        assertEquals("Customer with id: 1000000 has accounts and cannot be deleted", exception.getMessage());
        verify(customerRepository, times(1)).findById(1000000);
        verify(customerRepository, never()).delete(customer);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentCustomer() {
        // Arrange
        when(customerRepository.findById(1000000)).thenReturn(Optional.empty());

        // Act & Assert
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.deleteCustomer(1000000)
        );

        assertEquals("Customer with id: 1000000 not found", exception.getMessage());
        assertEquals("Customer with id: 1000000 not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(1000000);
        verify(customerRepository, never()).delete(any(Customer.class));
    }
}

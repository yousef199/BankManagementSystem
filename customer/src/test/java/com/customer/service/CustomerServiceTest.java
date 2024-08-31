package com.customer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.clients.account.AccountClient;
import com.clients.account.dto.KafkaDeleteAccountDTO;
import com.clients.account.dto.KafkaNewAccountDTO;
import com.clients.customer.dto.*;
import com.common.enums.AccountStatus;
import com.common.enums.AccountTypes;
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
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldCreateCustomerSuccessfully() {
        // given
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

        // when
        CustomerResponseDTO response = customerService.createCustomer(customerRequestDTO);

        // then
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
        // given
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

        // when
        CustomerResponseDTO response = customerService.getCustomer(1000000);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.httpStatus());
        assertEquals(customer.getCustomerId(), response.customerId());
        assertEquals("Customer retrieved successfully", response.message());

        verify(customerRepository, times(1)).findById(1000000);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        // given
        when(customerRepository.findById(1000000)).thenReturn(Optional.empty());

        // when & then
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomer(1000000)
        );

        assertEquals("Customer with id: 1000000 not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(1000000);
    }

    @Test
    void shouldReturnAllCustomersSuccessfully() {
        // given
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

        // when
        List<CustomerResponseDTO> responses = customerService.getAllCustomers();

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(customer.getCustomerId(), responses.get(0).customerId());
        assertEquals(HttpStatus.OK.value(), responses.get(0).httpStatus());

        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {
        // given
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

        // when
        CustomerUpdateResponseDTO response = customerService.updateCustomer(1000000, customerUpdateRequestDTO);

        // then
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
        // given
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

        // when & then
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
        // given
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

        // when
        CustomerDeleteResponseDTO response = customerService.deleteCustomer(1000000);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.httpStatus());
        assertEquals(1000000, response.customerId());
        assertEquals("Customer with id: 1000000 deleted successfully", response.message());

        verify(customerRepository, times(1)).findById(1000000);
        verify(customerRepository, times(1)).delete(customer);
    }

    @Test
    void shouldThrowExceptionWhenDeletingCustomerWithAccounts() {
        // given
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

        // when & then
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
        // given
        when(customerRepository.findById(1000000)).thenReturn(Optional.empty());

        // when & then
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.deleteCustomer(1000000)
        );

        assertEquals("Customer with id: 1000000 not found", exception.getMessage());
        assertEquals("Customer with id: 1000000 not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(1000000);
        verify(customerRepository, never()).delete(any(Customer.class));
    }

    @Test
    void shouldIncrementNumberOfAccountsWhenNewAccountEventReceived() {
        // Given
        KafkaNewAccountDTO kafkaNewAccountDTO = new KafkaNewAccountDTO(1000000123,
                1000000,
                AccountTypes.SAVINGS.getType(),
                AccountStatus.ACTIVE.getStatus()); // Assuming KafkaNewAccountDTO has a constructor with customerId
        Customer customer = new Customer();
        customer.setCustomerId(1000000);
        customer.setNumberOfAccounts(1);

        when(customerRepository.findById(1000000)).thenReturn(Optional.of(customer));

        // When
        customerService.handleNewAccountEvent(kafkaNewAccountDTO);

        // Then
        assertEquals(2, customer.getNumberOfAccounts());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void shouldDecrementNumberOfAccountsWhenDeleteAccountEventReceived() {
        // Given
        KafkaDeleteAccountDTO kafkaDeleteAccountDTO = new KafkaDeleteAccountDTO(1000000123,
                1000000); // Assuming KafkaDeleteAccountDTO has a constructor with customerId
        Customer customer = new Customer();
        customer.setCustomerId(1000000);
        customer.setNumberOfAccounts(1);

        when(customerRepository.findById(1000000)).thenReturn(Optional.of(customer));

        // When
        customerService.handleDeleteAccountEvent(kafkaDeleteAccountDTO);

        // Then
        assertEquals(0, customer.getNumberOfAccounts());
        verify(customerRepository, times(1)).save(customer);
    }

}

package com.account.service;

import com.account.entity.Account;
import com.account.exception.*;
import com.account.repository.AccountRepository;
import com.clients.account.dto.*;
import com.clients.customer.CustomerClient;
import com.clients.customer.dto.CustomerResponseDTO;
import com.clients.dto.GeneralResponseDTO;
import com.common.enums.AccountStatus;
import com.common.enums.AccountTypes;
import com.common.enums.CustomerStatus;
import com.common.enums.CustomerTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AccountService class.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private Random random;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccount_success() {
        int customerId = 1000000;
        int accountId = 1000000123;
        // Given
        AccountRequestDTO requestDTO = new AccountRequestDTO(customerId, BigDecimal.valueOf(1000), AccountTypes.SALARY.getType(), AccountStatus.ACTIVE.getStatus());
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO(HttpStatus.OK.value(),customerId , "John",
                "122333" ,
                CustomerTypes.CORPORATE.getType(),
                "Jordan" ,
                "0780709088" ,
                "John@gmail.com",
                1,
                CustomerStatus.ACTIVE.getStatus(),
                "Customer found successfully");
        when(customerClient.getCustomer(anyInt())).thenReturn(ResponseEntity.ok(customerResponseDTO));
        when(accountRepository.existsById(anyInt())).thenReturn(false);
        when(random.nextInt(1000)).thenReturn(123); // so that the accountId is 1000000123

        Account savedAccount = new Account(accountId, customerId, BigDecimal.valueOf(1000), "SAVINGS", "ACTIVE");
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // When
        AccountResponseDTO responseDTO = accountService.createAccount(requestDTO);

        // Then
        assertEquals(HttpStatus.CREATED.value(), responseDTO.httpStatus());
        assertEquals(accountId, responseDTO.accountId());
        assertEquals(customerId, responseDTO.customerId());
        assertEquals(BigDecimal.valueOf(1000), responseDTO.balance());
        assertEquals(AccountTypes.SALARY.getType(), responseDTO.accountType());
        assertEquals(AccountStatus.ACTIVE.getStatus(), responseDTO.accountStatus());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void createAccount_customerNotFound() {
        int customerId = 1000000;
        // Given
        AccountRequestDTO requestDTO = new AccountRequestDTO(customerId, BigDecimal.valueOf(1000), AccountTypes.SAVINGS.getType(), AccountStatus.ACTIVE.getStatus());
        when(customerClient.getCustomer(anyInt())).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

        // When / Then
        assertThrows(CustomerNotFoundException.class, () -> accountService.createAccount(requestDTO));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_maximumAccountsReached() {
        int customerId = 1000000;
        // Given
        AccountRequestDTO requestDTO = new AccountRequestDTO(1000000, BigDecimal.valueOf(1000), AccountTypes.SAVINGS.getType(), AccountStatus.ACTIVE.getStatus());
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO(HttpStatus.OK.value(),customerId , "John",
                "122333" ,
                CustomerTypes.CORPORATE.getType(),
                "Jordan" ,
                "0780709088" ,
                "John@gmail.com",
                10,
                CustomerStatus.ACTIVE.getStatus(),
                "Customer found successfully");
        when(customerClient.getCustomer(anyInt())).thenReturn(ResponseEntity.ok(customerResponseDTO));
        // When / Then
        assertThrows(MaximumNumberOfAccountsReachedException.class, () -> accountService.createAccount(requestDTO));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_salaryAccountAlreadyExists() {
        // Given
        int customerId = 1000000;
        // Given
        AccountRequestDTO requestDTO = new AccountRequestDTO(customerId, BigDecimal.valueOf(1000), AccountTypes.SALARY.getType(), AccountStatus.ACTIVE.getStatus());
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO(HttpStatus.OK.value(),customerId , "John",
                "122333" ,
                CustomerTypes.CORPORATE.getType(),
                "Jordan" ,
                "0780709088" ,
                "John@gmail.com",
                1,
                CustomerStatus.ACTIVE.getStatus(),
                "Customer found successfully");
        when(customerClient.getCustomer(anyInt())).thenReturn(ResponseEntity.ok(customerResponseDTO));
        when(accountRepository.findByCustomerId(anyInt())).thenReturn(List.of(new Account(1, 1, BigDecimal.valueOf(1000), AccountTypes.SALARY.getType(), AccountStatus.ACTIVE.getStatus())));

        // When / Then
        assertThrows(SalaryAccountAlreadyExistsException.class, () -> accountService.createAccount(requestDTO));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void getAccount_success() {
        int customerId = 1000000;
        int accountId = 1000000123;
        // Given
        Account account = new Account(accountId, customerId, BigDecimal.valueOf(1000), AccountTypes.SAVINGS.getType(), AccountStatus.ACTIVE.getStatus());
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(account));

        // When
        AccountResponseDTO responseDTO = accountService.getAccount(accountId);

        // Then
        assertEquals(HttpStatus.OK.value(), responseDTO.httpStatus());
        assertEquals(customerId, responseDTO.customerId());
        assertEquals(BigDecimal.valueOf(1000), responseDTO.balance());
        assertEquals(AccountTypes.SAVINGS.getType(), responseDTO.accountType());
        assertEquals(AccountStatus.ACTIVE.getStatus(), responseDTO.accountStatus());
    }

    @Test
    void getAccount_notFound() {
        int accountId = 1000000123;
        // Given
        when(accountRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(AccountNotFoundException.class, () -> accountService.getAccount(accountId));
    }

    @Test
    void updateAccount_success() {
        int customerId = 1000000;
        int accountId = 1000000123;
        // Given
        AccountUpdateRequestDTO updateRequestDTO = new AccountUpdateRequestDTO(null, BigDecimal.valueOf(2000), AccountTypes.INVESTMENT.getType(), AccountStatus.ACTIVE.getStatus());
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO(HttpStatus.OK.value(),customerId , "John",
                "122333" ,
                CustomerTypes.CORPORATE.getType(),
                "Jordan" ,
                "0780709088" ,
                "John@gmail.com",
                1,
                CustomerStatus.ACTIVE.getStatus(),
                "Customer found successfully");
        Account existingAccount = new Account(accountId, customerId, BigDecimal.valueOf(1000), AccountTypes.SAVINGS.getType(), AccountStatus.INACTIVE.getStatus());
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);
        when(customerClient.getCustomer(anyInt())).thenReturn(ResponseEntity.ok(customerResponseDTO));

        // When
        AccountUpdateResponseDTO responseDTO = accountService.updateAccount(accountId, updateRequestDTO);

        // Then
        assertEquals(HttpStatus.OK.value(), responseDTO.statusCode());
        assertEquals(3, responseDTO.updatedFields().size());
        assertEquals(AccountStatus.ACTIVE.getStatus(), responseDTO.updatedFields().get("accountStatus"));
        assertEquals(AccountTypes.INVESTMENT.getType(), responseDTO.updatedFields().get("accountType"));
        assertTrue(responseDTO.updatedFields().containsKey("balance"));
        assertEquals(BigDecimal.valueOf(2000), responseDTO.updatedFields().get("balance"));
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void transferAccount_success() {
        int oldCustomerId = 1000000;
        int newCustomerId = 1000001;
        int accountId = 1000000123;
        // Given
        AccountUpdateRequestDTO updateRequestDTO = new AccountUpdateRequestDTO(newCustomerId, BigDecimal.valueOf(2000), AccountTypes.INVESTMENT.getType(), AccountStatus.ACTIVE.getStatus());
        Account existingAccount = new Account(accountId, oldCustomerId, BigDecimal.valueOf(1000), AccountTypes.SAVINGS.getType(), AccountStatus.INACTIVE.getStatus());
        CustomerResponseDTO newCustomerResponseDto = new CustomerResponseDTO(HttpStatus.OK.value(), newCustomerId , "John",
                "122333" ,
                CustomerTypes.CORPORATE.getType(),
                "Jordan" ,
                "0780709088" ,
                "John@gmail.com",
                1,
                CustomerStatus.ACTIVE.getStatus(),
                "Customer found successfully");
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);
        when(customerClient.getCustomer(newCustomerId)).thenReturn(ResponseEntity.ok(newCustomerResponseDto));

        // When
        AccountUpdateResponseDTO responseDTO = accountService.updateAccount(accountId, updateRequestDTO);

        // Then
        assertEquals(HttpStatus.OK.value(), responseDTO.statusCode());
        assertEquals(4, responseDTO.updatedFields().size());
        assertEquals(AccountStatus.ACTIVE.getStatus(), responseDTO.updatedFields().get("accountStatus"));
        assertEquals(AccountTypes.INVESTMENT.getType(), responseDTO.updatedFields().get("accountType"));
        assertTrue(responseDTO.updatedFields().containsKey("balance"));
        assertEquals(BigDecimal.valueOf(2000), responseDTO.updatedFields().get("balance"));
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void transferAccount_unsuccessful_newCustomerInactive() {
        int oldCustomerId = 1000000;
        int newCustomerId = 1000001;
        int accountId = 1000000123;
        // Given
        AccountUpdateRequestDTO updateRequestDTO = new AccountUpdateRequestDTO(newCustomerId, BigDecimal.valueOf(2000), AccountTypes.INVESTMENT.getType(), AccountStatus.ACTIVE.getStatus());
        Account existingAccount = new Account(accountId, oldCustomerId, BigDecimal.valueOf(1000), AccountTypes.SAVINGS.getType(), AccountStatus.INACTIVE.getStatus());
        CustomerResponseDTO newCustomerResponseDto = new CustomerResponseDTO(HttpStatus.OK.value(), newCustomerId , "John",
                "122333" ,
                CustomerTypes.CORPORATE.getType(),
                "Jordan" ,
                "0780709088" ,
                "John@gmail.com",
                1,
                CustomerStatus.INACTIVE.getStatus(),
                "Customer found successfully");
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(existingAccount));
        when(customerClient.getCustomer(newCustomerId)).thenReturn(ResponseEntity.ok(newCustomerResponseDto));

        // When / Then
        assertThrows(InvalidAccountTransferRequest.class, () -> {
            // when
            accountService.updateAccount(accountId, updateRequestDTO);
        });
    }

    @Test
    void transferAccount_unsuccessful_newCustomerHasSalaryAccount() {
        int newCustomerId = 1000001;
        int oldCustomerId = 1000000;
        int accountId = 1000000123;
        List<Account> accountList = new ArrayList<>();
        // Given
        AccountUpdateRequestDTO updateRequestDTO = new AccountUpdateRequestDTO(newCustomerId, BigDecimal.valueOf(2000), AccountTypes.SALARY.getType(), AccountStatus.ACTIVE.getStatus());
        Account existingAccount = new Account(accountId, oldCustomerId, BigDecimal.valueOf(1000), AccountTypes.SAVINGS.getType(), AccountStatus.INACTIVE.getStatus());
        Account newCustomerExistingSalaryAccount = new Account(1000000124, newCustomerId, BigDecimal.valueOf(1000), AccountTypes.SALARY.getType(), AccountStatus.ACTIVE.getStatus());
        CustomerResponseDTO newCustomerResponseDto = new CustomerResponseDTO(HttpStatus.OK.value(), newCustomerId , "John",
                "122333" ,
                CustomerTypes.CORPORATE.getType(),
                "Jordan" ,
                "0780709088" ,
                "John@gmail.com",
                1,
                CustomerStatus.ACTIVE.getStatus(),
                "Customer found successfully");
        accountList.add(newCustomerExistingSalaryAccount);
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(existingAccount));
        when(customerClient.getCustomer(newCustomerId)).thenReturn(ResponseEntity.ok(newCustomerResponseDto));
        when(accountRepository.findByCustomerId(newCustomerId)).thenReturn(accountList);

        // When / Then
        assertThrows(InvalidAccountTransferRequest.class, () -> {
            // when
            accountService.updateAccount(accountId, updateRequestDTO);
        });
    }

    @Test
    void transferAccount_unsuccessful_newCustomerHasMaxAccounts() {
        int oldCustomerId = 1000000;
        int newCustomerId = 1000001;
        int accountId = 1000000123;
        // Given
        AccountUpdateRequestDTO updateRequestDTO = new AccountUpdateRequestDTO(newCustomerId, BigDecimal.valueOf(2000), AccountTypes.INVESTMENT.getType(), AccountStatus.ACTIVE.getStatus());
        Account existingAccount = new Account(accountId, oldCustomerId, BigDecimal.valueOf(1000), AccountTypes.SAVINGS.getType(), AccountStatus.INACTIVE.getStatus());
        CustomerResponseDTO newCustomerResponseDto = new CustomerResponseDTO(HttpStatus.OK.value(), newCustomerId , "John",
                "122333" ,
                CustomerTypes.CORPORATE.getType(),
                "Jordan" ,
                "0780709088" ,
                "John@gmail.com",
                10,
                CustomerStatus.ACTIVE.getStatus(),
                "Customer found successfully");
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(existingAccount));
        when(customerClient.getCustomer(newCustomerId)).thenReturn(ResponseEntity.ok(newCustomerResponseDto));

        // When / Then
        assertThrows(InvalidAccountTransferRequest.class, () -> {
            // when
            accountService.updateAccount(accountId, updateRequestDTO);
        });
    }

    @Test
    void transferAccount_sameCustomerId_throwsInvalidAccountTransferRequest() {
        // Given
        int customerId = 1000000;
        int accountId = 1000000123;
        AccountUpdateRequestDTO updateRequestDTO = new AccountUpdateRequestDTO(customerId, BigDecimal.valueOf(2000), AccountTypes.INVESTMENT.getType(), AccountStatus.ACTIVE.getStatus());
        Account existingAccount = new Account(accountId, customerId, BigDecimal.valueOf(1000), AccountTypes.SAVINGS.getType(), AccountStatus.INACTIVE.getStatus());
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(existingAccount));
        // Then
        assertThrows(InvalidAccountTransferRequest.class, () -> {
            // when
            accountService.updateAccount(accountId, updateRequestDTO);
        });
    }

    @Test
    void transferAcccount_newCustomerDoesntExist_throwsInvalidAccountTransferRequest() {
        // Given
        int customerId = 1000000;
        int newCustomerId = 1000001;
        int accountId = 1000000123;
        AccountUpdateRequestDTO updateRequestDTO = new AccountUpdateRequestDTO(newCustomerId, BigDecimal.valueOf(2000), AccountTypes.INVESTMENT.getType(), AccountStatus.ACTIVE.getStatus());
        Account existingAccount = new Account(accountId, customerId, BigDecimal.valueOf(1000), AccountTypes.SAVINGS.getType(), AccountStatus.INACTIVE.getStatus());
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(existingAccount));
        when(customerClient.getCustomer(newCustomerId)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        // Then
        assertThrows(InvalidAccountTransferRequest.class, () -> {
            // when
            accountService.updateAccount(accountId, updateRequestDTO);
        });
    }

    @Test
    void updateAccount_inactiveCustomer_throwsCannotActivateAccountException() {
        int customerId = 1000000;
        int accountId = 1000000123;
        // Given
        AccountUpdateRequestDTO updateRequestDTO = new AccountUpdateRequestDTO(null, BigDecimal.valueOf(2000), AccountTypes.INVESTMENT.getType(), AccountStatus.ACTIVE.getStatus());
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO(HttpStatus.OK.value(),customerId , "John",
                "122333" ,
                CustomerTypes.CORPORATE.getType(),
                "Jordan" ,
                "0780709088" ,
                "John@gmail.com",
                1,
                CustomerStatus.INACTIVE.getStatus(),
                "Customer found successfully");
        Account existingAccount = new Account(accountId, customerId, BigDecimal.valueOf(1000), AccountTypes.SAVINGS.getType(), AccountStatus.INACTIVE.getStatus());
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(existingAccount));
        when(customerClient.getCustomer(anyInt())).thenReturn(ResponseEntity.ok(customerResponseDTO));

        // Then
        assertThrows(CannotActivateAccountException.class, () -> {
            // when
            accountService.updateAccount(accountId, updateRequestDTO);
        });
    }

    @Test
    void updateAccount_salaryAccountExists_throwsSalaryAccountAlreadyExistsException() {
        int customerId = 1000000;
        int accountId = 1000000123;
        List<Account> accountList = new ArrayList<>();
        // Given
        AccountUpdateRequestDTO updateRequestDTO = new AccountUpdateRequestDTO(null, BigDecimal.valueOf(2000), AccountTypes.SALARY.getType(), AccountStatus.ACTIVE.getStatus());
        Account existingAccount = new Account(accountId, customerId, BigDecimal.valueOf(1000), AccountTypes.SALARY.getType(), AccountStatus.INACTIVE.getStatus());
        accountList.add(existingAccount);
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(existingAccount));
        when(accountRepository.findByCustomerId(anyInt())).thenReturn(accountList);

        // Then
        assertThrows(SalaryAccountAlreadyExistsException.class, () -> {
            // when
            accountService.updateAccount(accountId, updateRequestDTO);
        });
    }

    @Test
    void deleteAccount_success() {
        int accountId = 1000000123;
        int customerId = 1000000;
        // Given
        Account account = new Account(accountId, customerId, BigDecimal.valueOf(1000), "SAVINGS", "ACTIVE");
        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(account));

        // When
        AccountDeleteResponseDTO responseDTO = accountService.deleteAccount(accountId);

        // Then
        assertEquals(HttpStatus.OK.value(), responseDTO.statusCode());
        assertEquals("Account with id "+accountId+" deleted successfully", responseDTO.message());
        verify(accountRepository, times(1)).delete(any(Account.class));
    }

    @Test
    void deleteAccount_notFound() {
        int accountId = 1000000123;
        // Given
        when(accountRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(AccountNotFoundException.class, () -> accountService.deleteAccount(accountId));
        verify(accountRepository, never()).delete(any(Account.class));
    }
}

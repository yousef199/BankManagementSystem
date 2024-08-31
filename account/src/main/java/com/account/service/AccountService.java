package com.account.service;

import com.account.entity.Account;
import com.account.exception.AccountNotFoundException;
import com.account.exception.CustomerNotFoundException;
import com.account.exception.MaximumNumberOfAccountsReachedException;
import com.account.exception.SalaryAccountAlreadyExistsException;
import com.account.repository.AccountRepository;
import com.clients.account.dto.AccountRequestDTO;
import com.clients.account.dto.AccountResponseDTO;
import com.clients.account.dto.AccountUpdateRequestDTO;
import com.clients.account.dto.AccountUpdateResponseDTO;
import com.clients.customer.CustomerClient;
import com.clients.customer.dto.CustomerResponseDTO;
import com.clients.dto.GeneralResponseDTO;
import com.common.enums.AccountStatus;
import com.common.enums.AccountTypes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerClient customerClient;
    private final Random random = new Random();

    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {
        CustomerResponseDTO customerResponseDTO = customerClient.getCustomer(accountRequestDTO.customerId()).getBody();
        if (customerResponseDTO == null || (customerResponseDTO.httpStatus() != HttpStatus.OK.value())) {
            throw new CustomerNotFoundException("Customer with id: "+accountRequestDTO.customerId()+" not found.");
        }

        if (customerResponseDTO.numberOfAccounts() >= 10)
            throw new MaximumNumberOfAccountsReachedException("Customer has reached the maximum number of accounts. which is 10");

        if (accountRequestDTO.accountType().equalsIgnoreCase(AccountTypes.SALARY.getType()))
            checkIfCustomerHasSalaryAccount(accountRequestDTO.customerId());
        // Generate account ID
        int accountId = generateAccountId(accountRequestDTO.customerId());

        while (accountRepository.existsById(accountId)) {
            accountId = generateAccountId(accountRequestDTO.customerId());
        }
        // Create and save the new account
        Account newAccount = new Account();
        newAccount.setAccountId(accountId);
        newAccount.setCustomerId(accountRequestDTO.customerId());
        newAccount.setBalance(accountRequestDTO.balance());
        newAccount.setAccountType(accountRequestDTO.accountType());
        newAccount.setAccountStatus(accountRequestDTO.accountStatus()); // Default status

        accountRepository.save(newAccount);

        return new AccountResponseDTO(
                HttpStatus.CREATED.value(),  // HTTP status code
                newAccount.getAccountId(),
                newAccount.getCustomerId(),
                newAccount.getBalance(),
                newAccount.getAccountType(),
                newAccount.getAccountStatus(),
                "Account created successfully"
        );
    }

    public AccountResponseDTO getAccount(int accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with id "+accountId+" not found."));

        return new AccountResponseDTO(
                HttpStatus.OK.value(),  // HTTP status code
                account.getAccountId(),
                account.getCustomerId(),
                account.getBalance(),
                account.getAccountType(),
                account.getAccountStatus(),
                "Account retrieved successfully"
        );
    }

    public List<AccountResponseDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(account -> new AccountResponseDTO(
                        HttpStatus.OK.value(),  // HTTP status code
                        account.getAccountId(),
                        account.getCustomerId(),
                        account.getBalance(),
                        account.getAccountType(),
                        account.getAccountStatus(),
                        "Account retrieved successfully"
                ))
                .toList();
    }

    public List<AccountResponseDTO> getAccountsByCustomerId(int customerId) {
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        return accounts.stream()
                .map(account -> new AccountResponseDTO(
                        HttpStatus.OK.value(),  // HTTP status code
                        account.getAccountId(),
                        account.getCustomerId(),
                        account.getBalance(),
                        account.getAccountType(),
                        account.getAccountStatus(),
                        "Account retrieved successfully"
                ))
                .toList();
    }

    @Transactional
    public AccountUpdateResponseDTO updateAccount(int accountId, AccountUpdateRequestDTO accountUpdateRequestDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found."));

        // Map to store the updated fields
        Map<String, Object> updatedFields = new HashMap<>();

        // Update account details only if they are provided and store in the map
        if (accountUpdateRequestDTO.customerId() != null) {
            account.setCustomerId(accountUpdateRequestDTO.customerId());
            updatedFields.put("customerId", accountUpdateRequestDTO.customerId());
        }
        if (accountUpdateRequestDTO.balance() != null) {
            account.setBalance(accountUpdateRequestDTO.balance());
            updatedFields.put("balance", accountUpdateRequestDTO.balance());
        }
        if (accountUpdateRequestDTO.accountType() != null) {
            account.setAccountType(accountUpdateRequestDTO.accountType());
            updatedFields.put("accountType", accountUpdateRequestDTO.accountType());
        }
        if (accountUpdateRequestDTO.accountStatus() != null) {
            account.setAccountStatus(accountUpdateRequestDTO.accountStatus());
            updatedFields.put("accountStatus", accountUpdateRequestDTO.accountStatus());
        }

        accountRepository.save(account);

        // Return DTO with the map of updated fields
        return new AccountUpdateResponseDTO(
                HttpStatus.OK.value(),  // HTTP status code
                updatedFields,
                "Account with id "+accountId+" updated successfully"
        );
    }

    @Transactional
    public GeneralResponseDTO deleteAccount(int accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with id "+accountId+" not found."));

        accountRepository.delete(account);


        return new GeneralResponseDTO(
                HttpStatus.OK.value(),  // HTTP status code
                "Account with id "+accountId+"updated successfully"
        );
    }

    private void checkIfCustomerHasSalaryAccount(int customerId) {
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        accounts.stream()
                .filter(account -> account.getAccountType().equalsIgnoreCase(AccountTypes.SALARY.getType()))
                .findFirst()
                .ifPresent(account -> {
                    throw new SalaryAccountAlreadyExistsException("Customer already has a salary account");
                });
    }

    private int generateAccountId(int customerId) {
        int suffix = random.nextInt() * 1000;  // Generates a number between 0 and 999

        // Format the suffix to ensure it is always 3 digits
        String formattedSuffix = String.format("%03d", suffix);

        // Append the suffix to the customer ID
        return Integer.parseInt(customerId + formattedSuffix);
    }
}
package com.account.service;

import com.account.entity.Account;
import com.account.exception.AccountNotFoundException;
import com.account.repository.AccountRepository;
import com.clients.account.dto.AccountRequestDTO;
import com.clients.account.dto.AccountResponseDTO;
import com.clients.customer.CustomerClient;
import com.clients.customer.dto.CustomerResponseDTO;
import com.clients.dto.GeneralResponseDTO;
import com.common.enums.AccountStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerClient customerClient;

    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {
        // Validate customer existence
//        if (customerClient.getCustomer(accountRequestDTO.customerId())) {
//            throw new IllegalArgumentException("Customer ID does not exist.");
//        }

        CustomerResponseDTO customerResponseDTO = customerClient.getCustomer(accountRequestDTO.customerId()).getBody();
        // Generate account ID
//        String accountId = generateAccountId(accountRequestDTO.customerId());

        // Create and save the new account
        Account newAccount = new Account();
//        newAccount.setAccountId(accountId);
        newAccount.setCustomerId(accountRequestDTO.customerId());
        newAccount.setBalance(accountRequestDTO.initialBalance());
        newAccount.setAccountType(accountRequestDTO.accountType());
        newAccount.setAccountStatus(AccountStatus.ACTIVE.getStatus()); // Default status

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

    public AccountResponseDTO getAccount(String accountId) {
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

    public List<AccountResponseDTO> getAccountsByCustomerId(String customerId) {
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
    public GeneralResponseDTO updateAccount(String accountId, AccountRequestDTO accountRequestDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found."));

        // Update account details
        account.setBalance(accountRequestDTO.initialBalance());
        account.setAccountType(accountRequestDTO.accountType());

        accountRepository.save(account);

        return new GeneralResponseDTO(
                HttpStatus.OK.value(),  // HTTP status code
                "Account with id "+accountId+"updated successfully"
        );
    }

    @Transactional
    public GeneralResponseDTO deleteAccount(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with id "+accountId+" not found."));

        accountRepository.delete(account);


        return new GeneralResponseDTO(
                HttpStatus.OK.value(),  // HTTP status code
                "Account with id "+accountId+"updated successfully"
        );
    }
}
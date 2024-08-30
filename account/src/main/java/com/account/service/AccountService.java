package com.account.service;

import com.account.entity.Account;
import com.account.exception.AccountNotFoundException;
import com.account.exception.CustomerNotFoundException;
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
    //todo: check all the types of accounts as there could only be one salary account
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {
        CustomerResponseDTO customerResponseDTO = customerClient.getCustomer(accountRequestDTO.customerId()).getBody();
        if (customerResponseDTO != null && (customerResponseDTO.httpStatus() != HttpStatus.OK.value())) {
            throw new CustomerNotFoundException("Customer with id: "+accountRequestDTO.customerId()+" not found.");
        }
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
    //TODO: what if i dont want to update all fields
    public GeneralResponseDTO updateAccount(int accountId, AccountRequestDTO accountRequestDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found."));

        // Update account details
        account.setCustomerId(accountRequestDTO.customerId());
        account.setBalance(accountRequestDTO.balance());
        account.setAccountType(accountRequestDTO.accountType());
        account.setAccountStatus(accountRequestDTO.accountStatus());

        accountRepository.save(account);

        return new GeneralResponseDTO(
                HttpStatus.OK.value(),  // HTTP status code
                "Account with id "+accountId+"updated successfully"
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

    public int generateAccountId(int customerId) {
        int suffix = (int) (Math.random() * 1000);  // Generates a number between 0 and 999

        // Format the suffix to ensure it is always 3 digits
        String formattedSuffix = String.format("%03d", suffix);

        // Append the suffix to the customer ID
        return Integer.parseInt(customerId + formattedSuffix);
    }
}
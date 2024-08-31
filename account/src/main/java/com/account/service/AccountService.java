package com.account.service;

import com.account.entity.Account;
import com.account.exception.*;
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
import com.common.enums.CustomerStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class for managing accounts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerClient customerClient;
    private final Random random;

    /**
     * Creates a new account.
     *
     * @param accountRequestDTO the account request DTO containing the details for the new account
     * @return AccountResponseDTO the response DTO with the details of the created account
     * @throws CustomerNotFoundException if the customer does not exist
     * @throws MaximumNumberOfAccountsReachedException if the customer has reached the maximum number of accounts
     * @throws SalaryAccountAlreadyExistsException if the customer already has a salary account
     */
    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {
        CustomerResponseDTO customerResponseDTO = customerClient.getCustomer(accountRequestDTO.customerId()).getBody();
        validateCustomerForAccountCreation(accountRequestDTO, customerResponseDTO);

        int accountId = generateUniqueAccountId(accountRequestDTO.customerId());
        Account newAccount = buildAccount(accountId, accountRequestDTO, customerResponseDTO);

        accountRepository.save(newAccount);

        return new AccountResponseDTO(
                HttpStatus.CREATED.value(),
                newAccount.getAccountId(),
                newAccount.getCustomerId(),
                newAccount.getBalance(),
                newAccount.getAccountType(),
                newAccount.getAccountStatus(),
                "Account created successfully"
        );
    }

    /**
     * Retrieves an account by its ID.
     *
     * @param accountId the ID of the account
     * @return AccountResponseDTO the response DTO with the details of the retrieved account
     * @throws AccountNotFoundException if the account does not exist
     */
    public AccountResponseDTO getAccount(int accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + accountId + " not found."));

        return mapToAccountResponseDTO(account, "Account retrieved successfully");
    }

    /**
     * Retrieves all accounts.
     *
     * @return List<AccountResponseDTO> a list of response DTOs for all accounts
     */
    public List<AccountResponseDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(account -> mapToAccountResponseDTO(account, "Account retrieved successfully"))
                .toList();
    }

    /**
     * Retrieves accounts by customer ID.
     *
     * @param customerId the ID of the customer
     * @return List<AccountResponseDTO> a list of response DTOs for the customer's accounts
     */
    public List<AccountResponseDTO> getAccountsByCustomerId(int customerId) {
        return accountRepository.findByCustomerId(customerId).stream()
                .map(account -> mapToAccountResponseDTO(account, "Account retrieved successfully"))
                .toList();
    }

    /**
     * Updates an existing account.
     *
     * @param accountId the ID of the account to update
     * @param accountUpdateRequestDTO the DTO containing the fields to update
     * @return AccountUpdateResponseDTO the response DTO with the updated fields
     * @throws AccountNotFoundException if the account does not exist
     * @throws CustomerIdMustBeProvided if customer ID is required but not provided
     * @throws CannotActivateAccountException if an attempt is made to activate an account for an inactive customer
     */
    @Transactional
    public AccountUpdateResponseDTO updateAccount(int accountId, AccountUpdateRequestDTO accountUpdateRequestDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found."));

        Map<String, Object> updatedFields = updateAccountFields(account, accountUpdateRequestDTO);

        accountRepository.save(account);

        return new AccountUpdateResponseDTO(
                HttpStatus.OK.value(),
                updatedFields,
                "Account with id " + accountId + " updated successfully"
        );
    }

    /**
     * Deletes an account by its ID.
     *
     * @param accountId the ID of the account to delete
     * @return GeneralResponseDTO the response DTO confirming the deletion
     * @throws AccountNotFoundException if the account does not exist
     */
    @Transactional
    public GeneralResponseDTO deleteAccount(int accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + accountId + " not found."));

        accountRepository.delete(account);

        return new GeneralResponseDTO(
                HttpStatus.OK.value(),
                "Account with id " + accountId + " deleted successfully"
        );
    }

    /**
     * Checks if the customer already has a salary account.
     *
     * @param customerId the ID of the customer
     * @throws SalaryAccountAlreadyExistsException if the customer already has a salary account
     */
    private void checkIfCustomerHasSalaryAccount(int customerId) {
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        accounts.stream()
                .filter(account -> account.getAccountType().equalsIgnoreCase(AccountTypes.SALARY.getType()))
                .findFirst()
                .ifPresent(account -> {
                    throw new SalaryAccountAlreadyExistsException("Customer already has a salary account");
                });
    }

    /**
     * Generates a unique account ID.
     *
     * @param customerId the ID of the customer for which to generate the account ID
     * @return int the generated account ID
     */
    private int generateUniqueAccountId(int customerId) {
        int accountId;
        do {
            accountId = generateAccountId(customerId);
        } while (accountRepository.existsById(accountId));
        return accountId;
    }

    /**
     * Generates an account ID based on the customer ID.
     *
     * @param customerId the ID of the customer
     * @return int the generated account ID
     */
    private int generateAccountId(int customerId) {
        int suffix = random.nextInt(1000);  // Generates a number between 0 and 999
        String formattedSuffix = String.format("%03d", suffix);
        return Integer.parseInt(customerId + formattedSuffix);
    }

    /**
     * Builds a new account object.
     *
     * @param accountId the ID of the new account
     * @param accountRequestDTO the DTO containing the details for the new account
     * @param customerResponseDTO the customer response DTO for validation
     * @return Account the built account object
     */
    private Account buildAccount(int accountId, AccountRequestDTO accountRequestDTO, CustomerResponseDTO customerResponseDTO) {
        Account newAccount = new Account();
        newAccount.setAccountId(accountId);
        newAccount.setCustomerId(accountRequestDTO.customerId());
        newAccount.setBalance(accountRequestDTO.balance());
        newAccount.setAccountType(accountRequestDTO.accountType());

        if (accountRequestDTO.accountStatus().equalsIgnoreCase(AccountStatus.ACTIVE.getStatus()) &&
                customerResponseDTO.customerStatus().equalsIgnoreCase(CustomerStatus.INACTIVE.getStatus())) {
            log.warn("Customer with id: {} is inactive. Account status set to inactive as we cannot set an account to active for inactive customer", accountRequestDTO.customerId());
            newAccount.setAccountStatus(AccountStatus.INACTIVE.getStatus());
        } else {
            newAccount.setAccountStatus(accountRequestDTO.accountStatus());
        }

        return newAccount;
    }

    /**
     * Updates the account fields based on the update request DTO.
     *
     * @param account the account to update
     * @param accountUpdateRequestDTO the DTO containing the fields to update
     * @return Map<String, Object> a map of the updated fields
     */
    private Map<String, Object> updateAccountFields(Account account, AccountUpdateRequestDTO accountUpdateRequestDTO) {
        Map<String, Object> updatedFields = new HashMap<>();

        if (accountUpdateRequestDTO.customerId() != null) {
            validateCustomerForAccountUpdate(account.getCustomerId(), accountUpdateRequestDTO);
            account.setCustomerId(accountUpdateRequestDTO.customerId());
            updatedFields.put("customerId", accountUpdateRequestDTO.customerId());
        }
        if (accountUpdateRequestDTO.balance() != null) {
            account.setBalance(accountUpdateRequestDTO.balance());
            updatedFields.put("balance", accountUpdateRequestDTO.balance());
        }
        if (accountUpdateRequestDTO.accountType() != null) {
            validateAccountType(accountUpdateRequestDTO , account.getCustomerId());
            account.setAccountType(accountUpdateRequestDTO.accountType());
            updatedFields.put("accountType", accountUpdateRequestDTO.accountType());
        }
        if (accountUpdateRequestDTO.accountStatus() != null) {
            validateAccountStatus(account, accountUpdateRequestDTO);
            account.setAccountStatus(accountUpdateRequestDTO.accountStatus());
            updatedFields.put("accountStatus", accountUpdateRequestDTO.accountStatus());
        }

        return updatedFields;
    }

    /**
     * Validates the customer when creating a new account.
     *
     * @param accountRequestDTO the account request DTO
     * @param customerResponseDTO the customer response DTO
     * @throws CustomerNotFoundException if the customer does not exist
     * @throws MaximumNumberOfAccountsReachedException if the customer has reached the maximum number of accounts
     */
    private void validateCustomerForAccountCreation(AccountRequestDTO accountRequestDTO, CustomerResponseDTO customerResponseDTO) {
        if (customerResponseDTO == null || customerResponseDTO.httpStatus() != HttpStatus.OK.value()) {
            throw new CustomerNotFoundException("Customer with id " + accountRequestDTO.customerId() + " not found.");
        }

        if (customerResponseDTO.numberOfAccounts() >= 10) {
            throw new MaximumNumberOfAccountsReachedException("Customer has reached the maximum number of accounts.");
        }

        if (accountRequestDTO.accountType().equalsIgnoreCase(AccountTypes.SALARY.getType())) {
            checkIfCustomerHasSalaryAccount(accountRequestDTO.customerId());
        }
    }

    /**
     * Validates the customer when updating an account.
     *
     * @param accountUpdateRequestDTO the account update request DTO
     * @throws CustomerNotFoundException if the customer ID does not exist
     * @throws SalaryAccountAlreadyExistsException if the customer already has a salary account and the account being updated is also a salary account
     * @throws MaximumNumberOfAccountsReachedException if the customer has reached the maximum number of accounts
     */
    private void validateCustomerForAccountUpdate(Integer existingCustomerId, AccountUpdateRequestDTO accountUpdateRequestDTO) {
        Integer newCustomerId = accountUpdateRequestDTO.customerId();

        if (Objects.equals(existingCustomerId, newCustomerId)) {
            throw new InvalidAccountTransferRequest("New Customer ID must be provided when transferring account ownership , the provided customer ID is the same as the existing customer ID");
        }

        // Check if the new customer ID exists
        CustomerResponseDTO customerResponseDTO = customerClient.getCustomer(newCustomerId).getBody();
        if (customerResponseDTO == null || customerResponseDTO.httpStatus() != HttpStatus.OK.value()) {
            throw new InvalidAccountTransferRequest("Cannot Transfer Account to non Existing customer , Customer not found with id " + newCustomerId);
        }

        if (customerResponseDTO.customerStatus().equalsIgnoreCase(CustomerStatus.INACTIVE.getStatus())) {
            throw new InvalidAccountTransferRequest("Cannot transfer account to inactive customer");
        }
        // Check if the customer already has a salary account if updating to a salary account
        if ("SALARY".equalsIgnoreCase(accountUpdateRequestDTO.accountType())) {
            boolean hasSalaryAccount = accountRepository.findByCustomerId(newCustomerId)
                    .stream().anyMatch(acc -> "SALARY".equalsIgnoreCase(acc.getAccountType()));
            if (hasSalaryAccount) {
                throw new InvalidAccountTransferRequest("Customer to transfer account to already has a salary account");
            }
        }

        // Check if the customer has reached the maximum number of accounts
        if (customerResponseDTO.numberOfAccounts() >= 10) {
            throw new InvalidAccountTransferRequest("Customer to transfer account to has reached the maximum number of accounts");
        }
    }

    /**
     * Validates the account type.
     *
     * @param accountUpdateRequestDTO the account update request DTO
     * @throws SalaryAccountAlreadyExistsException if the customer already has a salary account
     */
    private void validateAccountType(AccountUpdateRequestDTO accountUpdateRequestDTO , int customerId) {
        if (accountUpdateRequestDTO.accountType().equalsIgnoreCase(AccountTypes.SALARY.getType())) {
            checkIfCustomerHasSalaryAccount(customerId);
        }
    }

    /**
     * Validates the account status.
     *
     * @param account the account to validate
     * @param accountUpdateRequestDTO the account update request DTO
     * @throws CannotActivateAccountException if an attempt is made to activate an account for an inactive customer
     */
    private void validateAccountStatus(Account account, AccountUpdateRequestDTO accountUpdateRequestDTO) {
        if (accountUpdateRequestDTO.accountStatus().equalsIgnoreCase(AccountStatus.ACTIVE.getStatus())) {
            CustomerResponseDTO customerResponseDTO = customerClient.getCustomer(account.getCustomerId()).getBody();
            if (customerResponseDTO != null && customerResponseDTO.customerStatus().equalsIgnoreCase(CustomerStatus.INACTIVE.getStatus())) {
                throw new CannotActivateAccountException("Cannot activate account for inactive customer.");
            }
        }
    }

    /**
     * Maps an account entity to an account response DTO.
     *
     * @param account the account entity
     * @param message the message to include in the response
     * @return AccountResponseDTO the mapped account response DTO
     */
    private AccountResponseDTO mapToAccountResponseDTO(Account account, String message) {
        return new AccountResponseDTO(
                HttpStatus.OK.value(),
                account.getAccountId(),
                account.getCustomerId(),
                account.getBalance(),
                account.getAccountType(),
                account.getAccountStatus(),
                message
        );
    }
}
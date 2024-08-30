package com.account.controller;

import com.account.entity.Account;
import com.account.kafka.KafkaProducerService;
import com.account.service.AccountService;
import com.clients.account.dto.AccountRequestDTO;
import com.clients.account.dto.AccountResponseDTO;
import com.common.enums.TopicNames;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api/v1/accounts")
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final KafkaProducerService kafkaProducerService;

    public AccountController(AccountService accountService , KafkaProducerService kafkaProducerService) {
        this.accountService = accountService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/registerAccount")
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO accountRequestDTO) {
        AccountResponseDTO accountResponseDTO = accountService.createAccount(accountRequestDTO);
        kafkaProducerService.sendMessage(TopicNames.ACCOUNT_NEW.getTopicName(), "new account created successfully");
        return new ResponseEntity<>(accountResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDTO> getAccount(@PathVariable String accountId) {
        AccountResponseDTO accountResponseDTO = accountService.getAccount(accountId);
        return ResponseEntity.ok(accountResponseDTO);
    }

    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
         List<AccountResponseDTO> allAccounts = accountService.getAllAccounts();
         return ResponseEntity.ok(allAccounts);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Account>> getAccountsByCustomerId(@PathVariable String customerId) {
        List<Account> accounts = accountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable String accountId, @Valid @RequestBody Account account) {
        Account updatedAccount = accountService.updateAccount(accountId, account);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
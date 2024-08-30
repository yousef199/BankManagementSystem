package com.account.controller;

import com.account.kafka.KafkaProducerService;
import com.account.service.AccountService;
import com.clients.account.dto.AccountRequestDTO;
import com.clients.account.dto.AccountResponseDTO;
import com.clients.dto.GeneralResponseDTO;
import com.common.enums.TopicNames;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final KafkaProducerService kafkaProducerService;

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

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
         List<AccountResponseDTO> allAccounts = accountService.getAllAccounts();
         return ResponseEntity.ok(allAccounts);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByCustomerId(@PathVariable String customerId) {
        List<AccountResponseDTO> accounts = accountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<GeneralResponseDTO> updateAccount(@PathVariable String accountId, @Valid @RequestBody AccountRequestDTO accountRequestDTO) {
        GeneralResponseDTO responseDTO = accountService.updateAccount(accountId, accountRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<GeneralResponseDTO> deleteAccount(@PathVariable String accountId) {
        GeneralResponseDTO responseDTO = accountService.deleteAccount(accountId);
        return ResponseEntity.ok(responseDTO);
    }
}
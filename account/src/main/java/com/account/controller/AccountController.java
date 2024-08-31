package com.account.controller;

import com.account.kafka.KafkaProducerService;
import com.account.service.AccountService;
import com.clients.account.dto.*;
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
        KafkaNewAccountDTO kafkaNewAccountDTO = new KafkaNewAccountDTO(accountResponseDTO.accountId(), accountResponseDTO.customerId() , accountResponseDTO.accountType() , accountResponseDTO.accountStatus());
        kafkaProducerService.sendMessage(TopicNames.ACCOUNT_NEW.getTopicName(), kafkaNewAccountDTO);
        return new ResponseEntity<>(accountResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDTO> getAccount(@PathVariable int accountId) {
        AccountResponseDTO accountResponseDTO = accountService.getAccount(accountId);
        return ResponseEntity.ok(accountResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
         List<AccountResponseDTO> allAccounts = accountService.getAllAccounts();
         return ResponseEntity.ok(allAccounts);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByCustomerId(@PathVariable int customerId) {
        List<AccountResponseDTO> accounts = accountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<AccountUpdateResponseDTO> updateAccount(@PathVariable int accountId, @RequestBody AccountUpdateRequestDTO accountUpdateRequestDTO) {
        AccountUpdateResponseDTO responseDTO = accountService.updateAccount(accountId, accountUpdateRequestDTO);
        KafkaUpdateAccountDTO kafkaUpdateAccountDTO = new KafkaUpdateAccountDTO(accountId , responseDTO.customerId() , responseDTO.updatedFields());
        kafkaProducerService.sendMessage(TopicNames.ACCOUNT_UPDATE.getTopicName(), kafkaUpdateAccountDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<AccountDeleteResponseDTO> deleteAccount(@PathVariable int accountId) {
        AccountDeleteResponseDTO responseDTO = accountService.deleteAccount(accountId);
        KafkaDeleteAccountDTO kafkaDeleteAccountDTO = new KafkaDeleteAccountDTO(accountId , responseDTO.customerId());
        kafkaProducerService.sendMessage(TopicNames.ACCOUNT_DELETE.getTopicName(), kafkaDeleteAccountDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
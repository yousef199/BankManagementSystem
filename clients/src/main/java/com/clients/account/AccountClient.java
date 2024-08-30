package com.clients.account;

import com.clients.account.dto.AccountResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "account")
public interface AccountClient {

    @GetMapping("/api/v1/accounts/{accountId}")
    ResponseEntity<AccountResponseDTO> getAccount(@PathVariable("accountId") String accountId);

    @GetMapping("/api/v1/accounts")
    ResponseEntity<List<AccountResponseDTO>> getAllAccounts();
}

package com.account.controller;

import com.account.kafka.KafkaProducerService;
import com.account.service.AccountService;
import com.clients.account.dto.*;
import com.common.enums.AccountStatus;
import com.common.enums.AccountTypes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    int customerId = 1000000;
    int accountId = 1000000123;

    private AccountRequestDTO accountRequestDTO;
    private AccountResponseDTO accountResponseDTO;
    private AccountUpdateRequestDTO accountUpdateRequestDTO;
    private AccountUpdateResponseDTO accountUpdateResponseDTO;
    private AccountDeleteResponseDTO accountDeleteResponseDTO;

    @BeforeEach
    public void setUp() {
        accountRequestDTO = new AccountRequestDTO(1000000, new BigDecimal(99), AccountTypes.SAVINGS.getType(), AccountStatus.ACTIVE.getStatus());
        accountResponseDTO = new AccountResponseDTO(HttpStatus.OK.value() , accountId , customerId , new BigDecimal(99), AccountTypes.SAVINGS.getType(), AccountStatus.ACTIVE.getStatus(), "message");
        accountUpdateRequestDTO = new AccountUpdateRequestDTO(new BigDecimal(99), AccountTypes.SAVINGS.getType(), AccountStatus.ACTIVE.getStatus());
        accountUpdateResponseDTO = new AccountUpdateResponseDTO(HttpStatus.OK.value() , customerId , new HashMap<>(Collections.singletonMap("field", "value")) , "message");
        accountDeleteResponseDTO = new AccountDeleteResponseDTO(HttpStatus.OK.value() , customerId , "message");
    }

    @Test
    void testCreateAccount() throws Exception {
        when(accountService.createAccount(any(AccountRequestDTO.class))).thenReturn(accountResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/registerAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(accountId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(customerId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(99))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountType").value(AccountTypes.SAVINGS.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountStatus").value(AccountStatus.ACTIVE.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("message"));

        verify(kafkaProducerService).sendMessage(anyString(), any(KafkaNewAccountDTO.class));
    }

    @Test
    void testGetAccount() throws Exception {
        when(accountService.getAccount(anyInt())).thenReturn(accountResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/account/" + accountId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(accountId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(customerId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(99))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountType").value(AccountTypes.SAVINGS.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountStatus").value(AccountStatus.ACTIVE.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("message"));
    }

    @Test
    void testGetAllAccounts() throws Exception {
        when(accountService.getAllAccounts()).thenReturn(Collections.singletonList(accountResponseDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountId").value(accountId))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].customerId").value(customerId))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].balance").value(99))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountType").value(AccountTypes.SAVINGS.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountStatus").value(AccountStatus.ACTIVE.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message").value("message"));
    }

    @Test
    void testGetAccountsByCustomerId() throws Exception {
        when(accountService.getAccountsByCustomerId(anyInt())).thenReturn(Collections.singletonList(accountResponseDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/customer/" + customerId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountId").value(accountId))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].customerId").value(customerId))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].balance").value(99))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountType").value(AccountTypes.SAVINGS.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountStatus").value(AccountStatus.ACTIVE.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message").value("message"));
    }

    @Test
    void testUpdateAccount() throws Exception {
        when(accountService.updateAccount(anyInt(), any(AccountUpdateRequestDTO.class))).thenReturn(accountUpdateResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/accounts/" + accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountUpdateRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(customerId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedFields.field").value("value"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("message"));

        verify(kafkaProducerService).sendMessage(anyString(), any(KafkaUpdateAccountDTO.class));
    }

    @Test
    void testDeleteAccount() throws Exception {
        when(accountService.deleteAccount(anyInt())).thenReturn(accountDeleteResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/accounts/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(customerId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("message"));

        verify(kafkaProducerService).sendMessage(anyString(), any(KafkaDeleteAccountDTO.class));
    }
}

package com.clients.customer;

import com.clients.customer.dto.CustomerResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "customer")
public interface CustomerClient {
    @GetMapping("api/v1/customers/{customerId}")
    ResponseEntity<CustomerResponseDTO> getCustomer(@PathVariable("customerId") int customerId);

    @GetMapping("api/v1/customers/")
    ResponseEntity<List<CustomerResponseDTO>> getAllCustomers();
}
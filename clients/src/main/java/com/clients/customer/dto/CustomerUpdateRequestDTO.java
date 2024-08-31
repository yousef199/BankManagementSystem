package com.clients.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerUpdateRequestDTO (
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        @Size(max = 50, message = "Legal ID cannot exceed 50 characters")
        String legalId,

        @Pattern(regexp = "retail|corporate|investment", message = "Customer type must be either retail, corporate, or investment")
        String type,

        @Size(max = 255, message = "Address cannot exceed 255 characters")
        String address,

        @Pattern(regexp = "^\\+?[0-9]{10,14}$", message = "Invalid phone number format")
        String phoneNumber,

        @Email(message = "Invalid email format")
        @Size(max = 150, message = "Email cannot exceed 150 characters")
        String email,

        @Pattern(regexp = "active|inactive", message = "Customer status must be either active or inactive")
        String customerStatus
){
}

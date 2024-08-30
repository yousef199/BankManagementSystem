package com.clients.dto;

public record ErrorResponseDTO(int httpStatus,
                               String message,
                               String error) {
}

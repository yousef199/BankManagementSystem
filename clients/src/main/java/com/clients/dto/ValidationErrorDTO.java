package com.clients.dto;

import java.util.Map;

public record ValidationErrorDTO(int status, Map<String, String> errors) {
}

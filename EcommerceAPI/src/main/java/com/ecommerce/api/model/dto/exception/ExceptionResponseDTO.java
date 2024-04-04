package com.ecommerce.api.model.dto.exception;

import org.springframework.http.HttpStatus;

public record ExceptionResponseDTO(Integer statusCode, HttpStatus status, String title, String message, String timestamp) {
}

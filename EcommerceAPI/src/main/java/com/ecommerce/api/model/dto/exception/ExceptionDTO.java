package com.ecommerce.api.model.dto.exception;

import org.springframework.http.HttpStatus;

public record ExceptionDTO(Integer statusCode, HttpStatus status, String title, String message, String timestamp) {
}

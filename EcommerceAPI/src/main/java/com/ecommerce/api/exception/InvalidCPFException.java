package com.ecommerce.api.exception;

public class InvalidCPFException extends RuntimeException {
    public InvalidCPFException() {
        super("CPF number is invalid!");
    }
}

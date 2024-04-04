package com.ecommerce.api.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException() {
        super("Amount number is invalid!");
    }
}

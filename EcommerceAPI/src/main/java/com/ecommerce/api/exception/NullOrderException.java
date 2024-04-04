package com.ecommerce.api.exception;

public class NullOrderException extends RuntimeException {
    public NullOrderException(String message) {
        super(message);
    }
}

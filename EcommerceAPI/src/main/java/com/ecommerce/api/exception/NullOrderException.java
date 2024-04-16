package com.ecommerce.api.exception;

public class NullOrderException extends RuntimeException {
    public NullOrderException() {
        super("Order doesn't exist!");
    }
    public NullOrderException(String message) {
        super(message);
    }
}

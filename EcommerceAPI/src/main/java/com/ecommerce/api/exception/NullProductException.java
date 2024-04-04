package com.ecommerce.api.exception;

public class NullProductException extends RuntimeException {
    public NullProductException() {
        super("Product doesn't exist!");
    }
}

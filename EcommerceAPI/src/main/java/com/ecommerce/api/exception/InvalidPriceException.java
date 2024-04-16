package com.ecommerce.api.exception;

public class InvalidPriceException extends RuntimeException {
    public InvalidPriceException() {
        super("Product price is invalid!");
    }
}

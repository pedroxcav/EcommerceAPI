package com.ecommerce.api.exception;
public class NullAddressException extends RuntimeException {
    public NullAddressException() {
        super("Address doesn't exist!");
    }
    public NullAddressException(String message) {
        super(message);
    }
}

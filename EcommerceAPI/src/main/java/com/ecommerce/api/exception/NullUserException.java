package com.ecommerce.api.exception;

public class NullUserException extends RuntimeException {
    public NullUserException() {
        super("User doesn't exist!");
    }
}

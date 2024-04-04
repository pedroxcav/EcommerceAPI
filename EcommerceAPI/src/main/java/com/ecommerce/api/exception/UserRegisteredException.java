package com.ecommerce.api.exception;

public class UserRegisteredException extends RuntimeException {
    public UserRegisteredException() {
        super("User already registered!");
    }
}

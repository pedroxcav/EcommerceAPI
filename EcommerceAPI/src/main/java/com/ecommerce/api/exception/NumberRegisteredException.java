package com.ecommerce.api.exception;

public class NumberRegisteredException extends RuntimeException{
    public NumberRegisteredException() {
        super("Number is already registered!");
    }
}

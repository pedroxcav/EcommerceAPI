package com.ecommerce.api.exception;

public class NullNumberException extends RuntimeException {
    public NullNumberException() {
        super("You don't have a number yet!");
    }
}

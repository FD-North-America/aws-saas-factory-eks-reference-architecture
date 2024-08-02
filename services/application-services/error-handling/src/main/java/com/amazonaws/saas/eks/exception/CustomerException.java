package com.amazonaws.saas.eks.exception;

public class CustomerException extends RuntimeException {
    public CustomerException(String message) {
        super(message);
    }
}

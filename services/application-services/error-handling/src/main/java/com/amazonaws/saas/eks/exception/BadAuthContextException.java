package com.amazonaws.saas.eks.exception;

public class BadAuthContextException extends RuntimeException {
    public BadAuthContextException(String message) {
        super(message);
    }
}

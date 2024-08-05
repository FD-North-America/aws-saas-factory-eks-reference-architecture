package com.amazonaws.saas.eks.exception;

public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }
}

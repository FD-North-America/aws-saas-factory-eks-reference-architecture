package com.amazonaws.saas.eks.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderId) {
        super(String.format("The order '%s' doesn't exist.", orderId));
    }
}

package com.amazonaws.saas.eks.exception;

public class OrderUpdateStatusInvalidException extends RuntimeException {
    public OrderUpdateStatusInvalidException(String orderId) {
        super(String.format("The order '%s' must be in a pending status.", orderId));
    }
}

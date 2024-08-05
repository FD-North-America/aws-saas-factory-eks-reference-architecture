package com.amazonaws.saas.eks.exception;

public class OrderPaymentFailure extends RuntimeException {
    public OrderPaymentFailure(String message) {
        super(message);
    }
}

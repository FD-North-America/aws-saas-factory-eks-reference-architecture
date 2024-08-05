package com.amazonaws.saas.eks.exception;

public class InvalidProductPricingRequestException extends RuntimeException {
    public InvalidProductPricingRequestException() {
        super("Invalid Product ID or Barcode");
    }
}

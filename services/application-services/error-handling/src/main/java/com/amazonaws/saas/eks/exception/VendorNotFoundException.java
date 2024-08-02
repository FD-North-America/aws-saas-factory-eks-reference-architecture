package com.amazonaws.saas.eks.exception;

public class VendorNotFoundException extends RuntimeException {
    public VendorNotFoundException(String vendorId, String storeId) {
        super(String.format("The vendor '%s' doesn't exist. StoreId: %s.",
                vendorId, storeId));
    }
}

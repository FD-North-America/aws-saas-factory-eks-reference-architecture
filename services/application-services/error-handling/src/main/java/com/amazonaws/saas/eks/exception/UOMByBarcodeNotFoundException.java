package com.amazonaws.saas.eks.exception;

public class UOMByBarcodeNotFoundException extends RuntimeException {
    public UOMByBarcodeNotFoundException(String barcode, String tenantId, String storeId) {
        super(String.format("The product UOM with barcode '%s' doesn't exist. TenantId: %s. StoreId: %s.",
                barcode, tenantId, storeId));
    }
}

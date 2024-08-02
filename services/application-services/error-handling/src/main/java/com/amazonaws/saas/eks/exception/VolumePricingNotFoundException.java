package com.amazonaws.saas.eks.exception;

public class VolumePricingNotFoundException extends RuntimeException {
    public VolumePricingNotFoundException(String volumePricingId, String tenantId, String storeId) {
        super(String.format("The volume pricing '%s' doesn't exist. TenantId: %s. StoreId: %s.",
                volumePricingId, tenantId, storeId));
    }
}

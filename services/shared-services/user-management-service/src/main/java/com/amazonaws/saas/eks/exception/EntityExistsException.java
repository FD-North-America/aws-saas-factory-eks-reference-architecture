package com.amazonaws.saas.eks.exception;

public class EntityExistsException extends RuntimeException {
    public EntityExistsException(String entityType, String entityName, String tenantId, String userPoolId) {
        super(String.format("The %s '%s' already exists. TenantId: %s. UserPoolId: %s.",
                entityType, entityName, tenantId, userPoolId));
    }
}

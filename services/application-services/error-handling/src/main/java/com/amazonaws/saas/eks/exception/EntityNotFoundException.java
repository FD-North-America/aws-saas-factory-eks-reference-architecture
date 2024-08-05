package com.amazonaws.saas.eks.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityType, String entityName, String tenantId, String userPoolId) {
        super(String.format("The %s '%s' doesn't exist. TenantId: %s. UserPoolId: %s.",
                entityType, entityName, tenantId, userPoolId));
    }

    public EntityNotFoundException(String entityType, String tenantId) {
        super(String.format("Does not exist for %s. TenantId: %s", entityType, tenantId));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}

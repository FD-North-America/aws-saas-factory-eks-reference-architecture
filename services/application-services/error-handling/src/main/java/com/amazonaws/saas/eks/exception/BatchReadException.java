package com.amazonaws.saas.eks.exception;

public class BatchReadException extends RuntimeException {
    public BatchReadException(String tenantId,  String entityType) {
        super(String.format("Error when batch reading entities of %s for TenantId %s", entityType, tenantId));
    }
}

package com.amazonaws.saas.eks.exception;

public class InvalidReportRequestException extends RuntimeException {
    public InvalidReportRequestException(String reason, String reportName, String tenantId) {
        super(String.format("%s. Report: %s. Tenant: %s", reason, reportName, tenantId));
    }
}

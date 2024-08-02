package com.amazonaws.saas.eks.exception;

public class ReportGenerationException extends RuntimeException {
    public ReportGenerationException(String reportName, String tenantId) {
        super(String.format("Error generating report '%s' for tenant %s", reportName, tenantId));
    }
}

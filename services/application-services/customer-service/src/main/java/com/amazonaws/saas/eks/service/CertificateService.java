package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.customer.dto.requests.certificate.CreateCertificateRequest;
import com.amazonaws.saas.eks.customer.dto.requests.certificate.ListCertificateRequestParams;
import com.amazonaws.saas.eks.customer.dto.requests.certificate.UpdateCertificateRequest;
import com.amazonaws.saas.eks.customer.dto.responses.certificate.CertificateResponse;
import com.amazonaws.saas.eks.customer.dto.responses.certificate.ListCertificateResponse;

public interface CertificateService {
    CertificateResponse create(String tenantId, CreateCertificateRequest request);

    CertificateResponse get(String certificateId, String tenantId);

    ListCertificateResponse getAll(String tenantId, ListCertificateRequestParams params);

    CertificateResponse update(String certificateId, String tenantId, UpdateCertificateRequest request);

    void delete(String certificateId, String tenantId);
}

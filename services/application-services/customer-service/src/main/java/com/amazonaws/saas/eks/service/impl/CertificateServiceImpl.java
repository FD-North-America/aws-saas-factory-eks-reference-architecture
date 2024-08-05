package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.customer.dto.requests.certificate.CreateCertificateRequest;
import com.amazonaws.saas.eks.customer.dto.requests.certificate.ListCertificateRequestParams;
import com.amazonaws.saas.eks.customer.dto.requests.certificate.UpdateCertificateRequest;
import com.amazonaws.saas.eks.customer.dto.responses.certificate.CertificateResponse;
import com.amazonaws.saas.eks.customer.dto.responses.certificate.ListCertificateResponse;
import com.amazonaws.saas.eks.customer.mapper.CertificateMapper;
import com.amazonaws.saas.eks.customer.model.Certificate;
import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.exception.InvalidArgumentsException;
import com.amazonaws.saas.eks.repository.CertificateRepository;
import com.amazonaws.saas.eks.repository.CustomerRepository;
import com.amazonaws.saas.eks.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Override
    public CertificateResponse create(String tenantId, CreateCertificateRequest request) {
        if (customerRepository.getById(request.getCustomerId(), tenantId) == null) {
            throw new EntityNotFoundException(String.format("Customer not found. ID: %s", request.getCustomerId()));
        }
        if (request.getExpiryDate().before(new Date())) {
            throw new InvalidArgumentsException("The expiration date has already expired");
        }
        Certificate certificate = CertificateMapper.INSTANCE.createCertificateRequestToCertificate(request);
        Certificate model = certificateRepository.create(certificate, tenantId);
        return CertificateMapper.INSTANCE.certificateToCertificateResponse(model);
    }

    @Override
    public CertificateResponse get(String certificateId, String tenantId) {
        Certificate certificate = certificateRepository.get(certificateId, tenantId);
        return CertificateMapper.INSTANCE.certificateToCertificateResponse(certificate);
    }

    @Override
    public ListCertificateResponse getAll(String tenantId, ListCertificateRequestParams params) {
        ListCertificateResponse response = new ListCertificateResponse();
        List<Certificate> models = certificateRepository.getAllByCustomerId(params.getCustomerId(), tenantId,
                params.getFilter());
        for (Certificate m: models) {
            response.getCertificates().add(CertificateMapper.INSTANCE.certificateToCertificateResponse(m));
        }
        return response;
    }

    @Override
    public CertificateResponse update(String certificateId, String tenantId, UpdateCertificateRequest request) {
        Certificate certificate = CertificateMapper.INSTANCE.updateCertificateRequestToCertificate(request);
        Certificate updatedCertificate = certificateRepository.update(certificateId, tenantId, certificate);
        return CertificateMapper.INSTANCE.certificateToCertificateResponse(updatedCertificate);
    }

    @Override
    public void delete(String certificateId, String tenantId) {
        // ToDo: Check that this certificate is not being used by any order's tax info
        certificateRepository.delete(certificateId, tenantId);
    }
}

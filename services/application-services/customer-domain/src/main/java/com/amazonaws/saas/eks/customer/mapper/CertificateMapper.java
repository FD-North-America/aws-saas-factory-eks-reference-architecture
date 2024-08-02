package com.amazonaws.saas.eks.customer.mapper;

import com.amazonaws.saas.eks.customer.dto.requests.certificate.CreateCertificateRequest;
import com.amazonaws.saas.eks.customer.dto.requests.certificate.UpdateCertificateRequest;
import com.amazonaws.saas.eks.customer.dto.responses.certificate.CertificateResponse;
import com.amazonaws.saas.eks.customer.model.Certificate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CertificateMapper {
    CertificateMapper INSTANCE = Mappers.getMapper(CertificateMapper.class);

    Certificate createCertificateRequestToCertificate(CreateCertificateRequest request);

    Certificate updateCertificateRequestToCertificate(UpdateCertificateRequest request);

    CertificateResponse certificateToCertificateResponse(Certificate certificate);
}

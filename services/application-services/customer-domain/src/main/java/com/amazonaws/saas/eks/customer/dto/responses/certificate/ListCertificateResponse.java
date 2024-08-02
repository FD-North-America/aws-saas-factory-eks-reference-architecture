package com.amazonaws.saas.eks.customer.dto.responses.certificate;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ListCertificateResponse {
    private List<CertificateResponse> certificates = new ArrayList<>();

    public int count() {
        return certificates.size();
    }
}

package com.amazonaws.saas.eks.customer.dto.requests.certificate;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class ListCertificateRequestParams {
    @NotEmpty
    private String customerId;

    private String filter;
}

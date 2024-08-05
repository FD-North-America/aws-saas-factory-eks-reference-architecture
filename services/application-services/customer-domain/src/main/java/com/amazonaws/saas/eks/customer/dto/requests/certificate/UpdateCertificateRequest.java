package com.amazonaws.saas.eks.customer.dto.requests.certificate;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateCertificateRequest {
    private String number;

    private String name;

    private Date expiryDate;
}

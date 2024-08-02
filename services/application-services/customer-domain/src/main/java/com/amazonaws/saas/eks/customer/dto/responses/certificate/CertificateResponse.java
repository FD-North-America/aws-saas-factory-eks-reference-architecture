package com.amazonaws.saas.eks.customer.dto.responses.certificate;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CertificateResponse {
    private String id;

    private String number;

    private String name;

    private Date expiryDate;

    private String customerId;

    private Date created;

    private Date modified;
}

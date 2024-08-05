package com.amazonaws.saas.eks.customer.dto.requests.certificate;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class CreateCertificateRequest {
    @NotEmpty
    private String number;

    @NotEmpty
    private String name;

    @NotNull
    private Date expiryDate;

    @NotNull
    private String customerId;
}

package com.amazonaws.saas.eks.settings.dto.requests.purchasing;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdatePurchasingSettingsRequest {
    @Size(max = 255)
    private String companyName;

    @Size(max = 255)
    private String branch;

    @Size(max = 255)
    private String address;

    @Size(max = 255)
    private String contact;

    @Size(max = 255)
    private String city;

    @Size(max = 255)
    private String phone;

    @Size(max = 255)
    private String state;

    @Size(max = 255)
    private String zip;

    @Size(max = 255)
    private String fax;

    @Size(max = 255)
    private String orderNumberFormat;

    private PurchasingOptions purchasingOptions;

    private ReceivingOptions receivingOptions;
}

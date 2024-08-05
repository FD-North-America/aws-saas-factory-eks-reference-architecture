package com.amazonaws.saas.eks.settings.dto.requests.purchasing;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CreatePurchasingSettingsRequest {
    @NotEmpty
    @Size(max = 255)
    private String companyName;

    @NotEmpty
    @Size(max = 255)
    private String branch;

    @NotEmpty
    @Size(max = 255)
    private String address;

    @Size(max = 255)
    private String contact;

    @NotEmpty
    @Size(max = 255)
    private String city;

    @NotEmpty
    @Size(max = 255)
    private String phone;

    @NotEmpty
    @Size(max = 255)
    private String state;

    @NotEmpty
    @Size(max = 255)
    private String zip;

    @Size(max = 255)
    private String fax;

    @NotEmpty
    @Size(max = 255)
    private String orderNumberFormat;

    @NotNull
    private PurchasingOptions purchasingOptions;

    @NotNull
    private ReceivingOptions receivingOptions;
}

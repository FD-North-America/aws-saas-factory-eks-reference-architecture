package com.amazonaws.saas.eks.settings.dto.responses;

import com.amazonaws.saas.eks.settings.model.v2.purchasing.PurchasingOptions;
import com.amazonaws.saas.eks.settings.model.v2.purchasing.ReceivingOptions;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PurchasingSettingsResponse {
    private String companyName;

    private String branch;

    private String address;

    private String contact;

    private String state;

    private String city;

    private String county;

    private String phone;

    private String zip;

    private String fax;

    private String orderNumberFormat;

    private PurchasingOptions purchasingOptions;

    private ReceivingOptions receivingOptions;

    private Date created;

    private Date modified;
}

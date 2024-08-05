package com.amazonaws.saas.eks.settings.dto.requests.salestax;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListSalesTaxSettingsRequestParams {
    private String filter;

    private String jurisdiction;

    private String state;

    private String city;
}

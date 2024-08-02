package com.amazonaws.saas.eks.settings.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ListSalesTaxSettingsResponse {
    private List<SalesTaxSettingsResponse> salesTaxes = new ArrayList<>();

    public int getCount() {
        int count = 0;
        for (SalesTaxSettingsResponse x: salesTaxes) {
            count += x.getCount();
        }
        return count;
    }
}

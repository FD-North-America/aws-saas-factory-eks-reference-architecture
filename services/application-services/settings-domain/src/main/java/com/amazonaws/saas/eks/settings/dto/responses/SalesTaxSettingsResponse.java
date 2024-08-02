package com.amazonaws.saas.eks.settings.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class SalesTaxSettingsResponse {
    private String id;

    private String code;

    private String description;

    private Float rate;

    private BigDecimal taxableLimit;

    private String taxingState;

    private String jurisdiction;

    private String mainSalesTaxId;

    private String subSalesTaxId;

    private Date created;

    private Date modified;

    private SalesTaxSettingsResponse[] salesTaxes;

    @JsonIgnore
    public int getCount() {
        int count = 1;
        if (salesTaxes != null) {
            for (SalesTaxSettingsResponse x: salesTaxes) {
                count += x.getCount();
            }
        }
        return count;
    }
}

package com.amazonaws.saas.eks.settings.model.v2.purchasing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchasingOptions {
    private Boolean updateQuoteCostAtPOSave;
    private Boolean updateMarketCostAtPOSave;
    private Boolean generateAutomaticBackorder;
}

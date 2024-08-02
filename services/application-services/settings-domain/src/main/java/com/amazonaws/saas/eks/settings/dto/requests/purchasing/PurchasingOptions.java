package com.amazonaws.saas.eks.settings.dto.requests.purchasing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchasingOptions {
    private boolean updateQuoteCostAtPOSave;
    private boolean updateMarketCostAtPOSave;
    private boolean generateAutomaticBackorder;
}

package com.amazonaws.saas.eks.settings.model.v2.purchasing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceivingOptions {
    private Boolean updateQuoteCost;
    private Boolean updateMarketCost;
    private Boolean updateQuoteCostForAllBranch;
    private Boolean updateMarketCostForAllBranch;
    private Boolean updateLastCostForAllBranch;
    private Boolean updateLastReceivedDateForAllBranch;
    private Boolean updateLandedCostForAllBranch;
    private Boolean autoGenerateVoucher;
}

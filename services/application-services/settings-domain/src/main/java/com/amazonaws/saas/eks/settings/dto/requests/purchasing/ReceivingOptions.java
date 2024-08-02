package com.amazonaws.saas.eks.settings.dto.requests.purchasing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceivingOptions {
    private boolean updateQuoteCost;
    private boolean updateMarketCost;
    private boolean updateQuoteCostForAllBranch;
    private boolean updateMarketCostForAllBranch;
    private boolean updateLastCostForAllBranch;
    private boolean updateLastReceivedDateForAllBranch;
    private boolean updateLandedCostForAllBranch;
    private boolean autoGenerateVoucher;
}

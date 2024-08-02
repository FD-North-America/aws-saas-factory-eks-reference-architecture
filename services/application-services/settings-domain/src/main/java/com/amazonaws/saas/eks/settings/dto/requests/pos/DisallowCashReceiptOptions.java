package com.amazonaws.saas.eks.settings.dto.requests.pos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisallowCashReceiptOptions {
    private boolean onSpecialInstruction;
    private boolean onReturnItems;
    private boolean onNonTaxable;
    private boolean onLoadedItems;
}

package com.amazonaws.saas.eks.settings.model.v2.pos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisallowCashReceiptOptions {
    private Boolean onSpecialInstruction;
    private Boolean onReturnItems;
    private Boolean onNonTaxable;
    private Boolean onLoadedItems;
}

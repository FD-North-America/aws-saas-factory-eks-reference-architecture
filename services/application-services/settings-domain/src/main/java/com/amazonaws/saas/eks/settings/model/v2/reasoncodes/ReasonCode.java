package com.amazonaws.saas.eks.settings.model.v2.reasoncodes;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReasonCode {
    private String code;

    private String description;

    private boolean taxable;

    private boolean returnToInventory;

    private boolean pointOfSale;

    private boolean inventory;

    private boolean purchasing;

    private boolean accountsReceivable;
}

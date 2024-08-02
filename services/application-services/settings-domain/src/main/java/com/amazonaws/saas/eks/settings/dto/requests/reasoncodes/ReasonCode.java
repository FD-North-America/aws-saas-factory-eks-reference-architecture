package com.amazonaws.saas.eks.settings.dto.requests.reasoncodes;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
public class ReasonCode {
    @NotBlank
    @Size(max = 10)
    private String code;

    @Size(max = 20)
    private String description;

    private boolean taxable;

    private boolean returnToInventory;

    private boolean pointOfSale;

    private boolean inventory;

    private boolean purchasing;

    private boolean accountsReceivable;
}

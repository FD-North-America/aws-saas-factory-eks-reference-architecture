package com.amazonaws.saas.eks.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

public class CashDrawerCheckoutReportRequest {
    @NotEmpty
    @Getter
    @Setter
    private String cashDrawerNumber;

    @NotEmpty
    @Getter
    @Setter
    private String checkoutDate;
}

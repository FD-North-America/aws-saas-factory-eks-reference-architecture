package com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PaidOutCodesAndDiscountsReportResponse {
    private PaidOutCodesResponse paidOutCodes = new PaidOutCodesResponse();

    private DiscountListResponse discounts = new DiscountListResponse();
}

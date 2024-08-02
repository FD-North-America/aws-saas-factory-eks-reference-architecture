package com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DiscountListResponse {
    private List<DiscountResponse> items = new ArrayList<>();

    private BigDecimal totalAmount = BigDecimal.ZERO;
}

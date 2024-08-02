package com.amazonaws.saas.eks.dto.responses.categorySales;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TotalRow {
    private String name;
    private int quantitySold;
    private float quantityOnHand;
    private BigDecimal totalSales = BigDecimal.ZERO;
    private BigDecimal cost = BigDecimal.ZERO;
    private BigDecimal profit = BigDecimal.ZERO;
    private BigDecimal grossMargin = BigDecimal.ZERO;
}

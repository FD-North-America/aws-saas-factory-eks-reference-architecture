package com.amazonaws.saas.eks.generator.categorySales;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductOrderTotals {
    private int quantitySold;
    private BigDecimal totalSales = BigDecimal.ZERO;
    private BigDecimal cost = BigDecimal.ZERO;
    private BigDecimal profit = BigDecimal.ZERO;
    private BigDecimal grossMargin = BigDecimal.ZERO;
}

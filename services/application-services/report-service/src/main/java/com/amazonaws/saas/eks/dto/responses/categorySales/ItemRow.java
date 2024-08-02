package com.amazonaws.saas.eks.dto.responses.categorySales;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemRow {
    private String id;
    private String description;
    private int quantitySold;
    private String pricingUom;
    private float quantityOnHand;
    private String stockingUom;
    private float minOnHandQuantity;
    private BigDecimal totalSales = BigDecimal.ZERO;
    private BigDecimal cost = BigDecimal.ZERO;
    private BigDecimal profit = BigDecimal.ZERO;
    private BigDecimal grossMargin = BigDecimal.ZERO;
}

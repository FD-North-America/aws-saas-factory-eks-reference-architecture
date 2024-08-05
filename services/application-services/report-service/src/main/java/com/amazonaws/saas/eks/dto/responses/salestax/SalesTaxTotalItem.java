package com.amazonaws.saas.eks.dto.responses.salestax;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesTaxTotalItem {
    private String taxCode;
    private String taxCodeDescription;
    private BigDecimal taxRate;
    private BigDecimal gross;
    private BigDecimal nonTaxable;
    private BigDecimal taxable;
    private BigDecimal taxDue;
}

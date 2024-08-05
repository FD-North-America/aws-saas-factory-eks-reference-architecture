package com.amazonaws.saas.eks.dto.responses.salestax;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class SalesTaxItem {
    private String invoiceNumber;
    private Date invoiceDate;
    private BigDecimal invoiceAmount;
    private BigDecimal invoiceTax;
    private BigDecimal invoiceTransactionAmount;
    private BigDecimal nonTaxableSales;
    private BigDecimal taxableSales;
    private BigDecimal taxLiability;
}

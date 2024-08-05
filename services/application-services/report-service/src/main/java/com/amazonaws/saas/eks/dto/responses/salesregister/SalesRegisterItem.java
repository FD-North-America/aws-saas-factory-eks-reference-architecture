package com.amazonaws.saas.eks.dto.responses.salesregister;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

import static com.amazonaws.saas.eks.util.Utils.roundValue;

@Getter
@Setter
public class SalesRegisterItem {
    private String branch;
    private String invoiceNumber;
    private Date invoiceDate;
    private BigDecimal sales = roundValue(BigDecimal.ZERO);
    private BigDecimal cost = roundValue(BigDecimal.ZERO);
    private BigDecimal taxTotals = roundValue(BigDecimal.ZERO);
    private BigDecimal invoiceAmount = roundValue(BigDecimal.ZERO);
    private BigDecimal profit = roundValue(BigDecimal.ZERO);
    private BigDecimal margin = roundValue(BigDecimal.ZERO);
    private String salesRep;
}

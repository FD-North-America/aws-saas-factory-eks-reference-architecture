package com.amazonaws.saas.eks.dto.responses.cashdrawercheckoutreport;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CashOutCount {
    private BigDecimal cashCounted;
    private BigDecimal cashCalculated;
    private BigDecimal cashOverShort;

    private BigDecimal startUpAmountCounted;
    private BigDecimal startUpAmountCalculated;
    private BigDecimal startUpAmountShort;

    private BigDecimal paidOutCounted;
    private BigDecimal paidOutCalculated;
    private BigDecimal paidOutShort;

    private BigDecimal cashSubtotalCounted;
    private BigDecimal cashSubtotalCalculated;
    private BigDecimal cashSubtotalShort;

    private BigDecimal ccCounted;
    private BigDecimal ccCalculated;
    private BigDecimal ccShort;

    private BigDecimal totalCounted;
    private BigDecimal totalCalculated;
    private BigDecimal totalShort;
}

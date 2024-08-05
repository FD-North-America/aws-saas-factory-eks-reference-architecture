package com.amazonaws.saas.eks.dto.responses.cashdrawercheckoutreport;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CashDrawerTransactionItem {
    private String transactionType;
    private String invoiceNumber;
    private String tenderType;
    private String tenderNumber;
    private BigDecimal amount;
}

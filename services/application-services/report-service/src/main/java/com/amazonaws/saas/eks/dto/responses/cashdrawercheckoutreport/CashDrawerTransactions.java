package com.amazonaws.saas.eks.dto.responses.cashdrawercheckoutreport;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CashDrawerTransactions {
    private List<CashDrawerTransactionItem> items = new ArrayList<>();
    private BigDecimal cashSubtotal = BigDecimal.ZERO;
}

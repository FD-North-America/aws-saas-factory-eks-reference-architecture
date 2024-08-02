package com.amazonaws.saas.eks.dto.responses.salesregister;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static com.amazonaws.saas.eks.util.Utils.roundValue;

@Getter
@Setter
@Builder
public class CashItem {
    private String type;
    @Builder.Default
    private BigDecimal receipts = roundValue(BigDecimal.ZERO);
    @Builder.Default
    private BigDecimal returns = roundValue(BigDecimal.ZERO);
    @Builder.Default
    private BigDecimal cashTotals = roundValue(BigDecimal.ZERO);
}

package com.amazonaws.saas.eks.dto.responses.salesregister;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static com.amazonaws.saas.eks.util.Utils.roundValue;

@Getter
@Setter
@Builder
public class ReportDetailTotal {
    private String type;
    @Builder.Default
    private BigDecimal sales = roundValue(BigDecimal.ZERO);
    @Builder.Default
    private BigDecimal taxTotals = roundValue(BigDecimal.ZERO);
    @Builder.Default
    private BigDecimal invAmount = roundValue(BigDecimal.ZERO);
    @Builder.Default
    private BigDecimal profit = roundValue(BigDecimal.ZERO);
    @Builder.Default
    private BigDecimal margin = roundValue(BigDecimal.ZERO);
}

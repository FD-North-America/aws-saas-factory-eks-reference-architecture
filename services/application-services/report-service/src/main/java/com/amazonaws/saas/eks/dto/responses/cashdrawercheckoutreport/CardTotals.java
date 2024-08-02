package com.amazonaws.saas.eks.dto.responses.cashdrawercheckoutreport;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CardTotals {
    private Map<String, BigDecimal> totalByCard = new HashMap<>();

    private BigDecimal total = BigDecimal.ZERO;
}

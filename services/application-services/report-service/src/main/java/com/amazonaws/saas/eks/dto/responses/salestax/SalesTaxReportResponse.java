package com.amazonaws.saas.eks.dto.responses.salestax;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SalesTaxReportResponse {
    private List<SalesTaxItem> items = new ArrayList<>();
    private long count;
    private List<SalesTaxTotalItem> totals = new ArrayList<>();
}

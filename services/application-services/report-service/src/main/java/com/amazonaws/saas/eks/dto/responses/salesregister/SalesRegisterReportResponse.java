package com.amazonaws.saas.eks.dto.responses.salesregister;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SalesRegisterReportResponse {
    private List<SalesRegisterItem> items = new ArrayList<>();
    private long count;
    private List<ReportDetailTotal> totalItems = new ArrayList<>();
    private List<CashItem> cashItems = new ArrayList<>();
    private BigDecimal cashSales;
    private BigDecimal immediateDiscounts;
}

package com.amazonaws.saas.eks.dto.responses.stockstatus;

import com.amazonaws.saas.eks.dto.responses.salestax.SalesTaxTotalItem;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StockStatusReportResponse {
    private List<StockStatusItem> items = new ArrayList<>();
    private long count;
}

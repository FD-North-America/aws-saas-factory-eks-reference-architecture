package com.amazonaws.saas.eks.product.dto.responses.saleshistory;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ListSalesHistoryResponse {
    private List<SalesHistoryResponse> saleHistories = new ArrayList<>();
    private long count;
}

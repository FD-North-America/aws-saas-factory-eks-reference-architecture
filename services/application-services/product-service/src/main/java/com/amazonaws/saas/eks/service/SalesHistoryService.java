package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.product.dto.responses.saleshistory.ListSalesHistoryResponse;

public interface SalesHistoryService {
    ListSalesHistoryResponse getByProduct(String tenantId, String productId);
}

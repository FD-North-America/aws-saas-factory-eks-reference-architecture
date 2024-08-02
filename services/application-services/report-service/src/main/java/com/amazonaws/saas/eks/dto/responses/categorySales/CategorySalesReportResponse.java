package com.amazonaws.saas.eks.dto.responses.categorySales;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CategorySalesReportResponse {
    private List<CategorySaleResponse> categories = new ArrayList<>();
    private TotalRow allCategoryTotals;
}

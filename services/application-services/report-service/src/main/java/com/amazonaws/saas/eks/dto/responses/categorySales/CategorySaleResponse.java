package com.amazonaws.saas.eks.dto.responses.categorySales;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CategorySaleResponse {
    private String categoryName;
    private String path;
    private List<ItemRow> items = new ArrayList<>();
    private TotalRow totals;
}

package com.amazonaws.saas.eks.product.dto.responses.category;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ListCategoriesResponse {
    @Getter
    @Setter
    private List<CategoryResponse> categories = new ArrayList<>();

    public int getCount() {
        int count = 0;
        for (CategoryResponse cr: categories) {
            count += cr.getCount();
        }
        return count;
    }
}

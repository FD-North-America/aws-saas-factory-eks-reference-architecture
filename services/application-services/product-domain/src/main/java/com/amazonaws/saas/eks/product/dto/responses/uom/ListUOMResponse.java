package com.amazonaws.saas.eks.product.dto.responses.uom;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ListUOMResponse {
    @Getter
    @Setter
    private List<UOMResponse> unitsOfMeasure = new ArrayList<>();

    public int getCount() {
        return unitsOfMeasure.size();
    }
}

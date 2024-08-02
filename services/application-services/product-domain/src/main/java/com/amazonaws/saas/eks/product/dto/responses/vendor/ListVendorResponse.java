package com.amazonaws.saas.eks.product.dto.responses.vendor;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ListVendorResponse {
    private List<ListVendorItemResponse> vendors = new ArrayList<>();
    private long count;
}

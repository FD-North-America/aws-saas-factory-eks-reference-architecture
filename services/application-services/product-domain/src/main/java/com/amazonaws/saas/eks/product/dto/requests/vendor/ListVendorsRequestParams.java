package com.amazonaws.saas.eks.product.dto.requests.vendor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListVendorsRequestParams {
    private Integer from = 0;
    private Integer size = 0;
    private String sortBy;
    private String filter;
}

package com.amazonaws.saas.eks.dto.requests.product;

import lombok.Getter;
import lombok.Setter;

public class ListProductRequestParams {
    @Getter
    @Setter
    private Integer from;

    @Getter
    @Setter
    private Integer size;

    @Getter
    @Setter
    private String categoryId;

    @Getter
    @Setter
    private String filter;

    @Getter
    @Setter
    private String sortBy;

    @Getter
    @Setter
    private String vendorId;

    @Getter
    @Setter
    private String barcode;
}

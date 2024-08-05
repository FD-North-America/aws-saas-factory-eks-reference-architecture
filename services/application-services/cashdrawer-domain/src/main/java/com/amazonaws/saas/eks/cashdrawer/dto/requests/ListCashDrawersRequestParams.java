package com.amazonaws.saas.eks.cashdrawer.dto.requests;

import lombok.Getter;
import lombok.Setter;

public class ListCashDrawersRequestParams {
    @Getter
    @Setter
    private Integer from;

    @Getter
    @Setter
    private Integer size;

    @Getter
    @Setter
    private String filter;

    @Getter
    @Setter
    private String sortBy;
}

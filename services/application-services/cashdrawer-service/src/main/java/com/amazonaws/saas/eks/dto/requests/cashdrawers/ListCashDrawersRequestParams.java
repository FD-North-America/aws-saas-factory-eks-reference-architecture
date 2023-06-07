package com.amazonaws.saas.eks.dto.requests.cashdrawers;

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

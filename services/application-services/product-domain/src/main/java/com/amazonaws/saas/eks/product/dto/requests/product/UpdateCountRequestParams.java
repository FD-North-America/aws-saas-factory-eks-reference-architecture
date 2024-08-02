package com.amazonaws.saas.eks.product.dto.requests.product;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UpdateCountRequestParams {
    @Getter
    @Setter
    private List<UpdateProductCountRequest> productCountRequests;
}

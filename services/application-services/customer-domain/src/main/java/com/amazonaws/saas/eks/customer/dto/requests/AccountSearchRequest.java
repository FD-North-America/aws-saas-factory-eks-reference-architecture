package com.amazonaws.saas.eks.customer.dto.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountSearchRequest {
    private Integer from;
    private Integer size;
    private String filter;
    private String sortBy;
}

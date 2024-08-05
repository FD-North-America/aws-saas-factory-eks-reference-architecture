package com.amazonaws.saas.eks.customer.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ListAccountsResponse {
    private List<AccountResponse> accounts = new ArrayList<>();
    private long count;
}

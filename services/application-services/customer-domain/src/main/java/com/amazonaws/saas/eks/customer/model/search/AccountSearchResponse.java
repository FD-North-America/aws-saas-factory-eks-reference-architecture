package com.amazonaws.saas.eks.customer.model.search;

import com.amazonaws.saas.eks.customer.model.Account;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AccountSearchResponse {
    private List<Account> accounts = new ArrayList<>();
    private long count;
}

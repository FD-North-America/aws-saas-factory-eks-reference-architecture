package com.amazonaws.saas.eks.customer.model.search;

import com.amazonaws.saas.eks.customer.model.Customer;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CustomerSearchResponse {
    private List<Customer> customers = new ArrayList<>();
    private long count;
}

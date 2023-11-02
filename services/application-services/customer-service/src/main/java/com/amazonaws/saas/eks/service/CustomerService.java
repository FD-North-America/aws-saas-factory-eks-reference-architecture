package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.customer.dto.requests.CreateCustomerRequest;
import com.amazonaws.saas.eks.customer.dto.responses.CustomerResponse;

public interface CustomerService {
    CustomerResponse create(CreateCustomerRequest request, String tenantId);
}

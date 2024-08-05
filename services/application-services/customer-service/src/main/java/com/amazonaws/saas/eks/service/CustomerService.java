package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.customer.dto.requests.AccountSearchRequest;
import com.amazonaws.saas.eks.customer.dto.requests.CreateCustomerRequest;
import com.amazonaws.saas.eks.customer.dto.requests.CustomerSearchRequest;
import com.amazonaws.saas.eks.customer.dto.requests.UpdateCustomerRequest;
import com.amazonaws.saas.eks.customer.dto.responses.CustomerResponse;
import com.amazonaws.saas.eks.customer.dto.responses.ListAccountsResponse;
import com.amazonaws.saas.eks.customer.dto.responses.ListCustomersResponse;

public interface CustomerService {
    CustomerResponse create(CreateCustomerRequest request, String tenantId);
    CustomerResponse read(String customerId, String tenantId);
    CustomerResponse update(UpdateCustomerRequest request, String customerId, String tenantId);
    void delete(String customerId, String tenantId);
    ListCustomersResponse search(CustomerSearchRequest request, String tenantId);
    ListAccountsResponse searchCustomerAccounts(AccountSearchRequest request, String customerId, String tenantId);
}

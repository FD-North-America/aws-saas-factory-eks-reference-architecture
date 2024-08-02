package com.amazonaws.saas.eks.customer.mapper;

import com.amazonaws.saas.eks.customer.dto.requests.CreateCustomerRequest;
import com.amazonaws.saas.eks.customer.dto.requests.UpdateCustomerRequest;
import com.amazonaws.saas.eks.customer.dto.responses.CustomerResponse;
import com.amazonaws.saas.eks.customer.dto.responses.ListAccountsResponse;
import com.amazonaws.saas.eks.customer.dto.responses.ListCustomersResponse;
import com.amazonaws.saas.eks.customer.model.Account;
import com.amazonaws.saas.eks.customer.model.Customer;
import com.amazonaws.saas.eks.customer.model.search.AccountSearchResponse;
import com.amazonaws.saas.eks.customer.model.search.CustomerSearchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    Customer createCustomerRequestToCustomer(CreateCustomerRequest request);

    CustomerResponse customerToCustomerResponse(Customer customer);

    @Mapping(target = "partitionKey", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Account customerToAccount(Customer customer);

    Customer updateCustomerRequestToCustomer(UpdateCustomerRequest request);
    ListCustomersResponse customerSearchResponseToListCustomerResponse(CustomerSearchResponse response);
    ListAccountsResponse accountSearchResponseToListAccountsResponse(AccountSearchResponse response);
}

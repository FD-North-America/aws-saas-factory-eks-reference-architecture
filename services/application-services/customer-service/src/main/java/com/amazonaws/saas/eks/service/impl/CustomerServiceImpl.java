package com.amazonaws.saas.eks.service.impl;


import com.amazonaws.saas.eks.customer.dto.requests.AccountSearchRequest;
import com.amazonaws.saas.eks.customer.dto.requests.CreateCustomerRequest;
import com.amazonaws.saas.eks.customer.dto.requests.CustomerSearchRequest;
import com.amazonaws.saas.eks.customer.dto.requests.UpdateCustomerRequest;
import com.amazonaws.saas.eks.customer.dto.responses.CustomerResponse;
import com.amazonaws.saas.eks.customer.dto.responses.ListAccountsResponse;
import com.amazonaws.saas.eks.customer.dto.responses.ListCustomersResponse;
import com.amazonaws.saas.eks.customer.mapper.CustomerMapper;
import com.amazonaws.saas.eks.customer.model.Account;
import com.amazonaws.saas.eks.customer.model.Customer;
import com.amazonaws.saas.eks.customer.model.enums.EntityType;
import com.amazonaws.saas.eks.customer.model.search.AccountSearchResponse;
import com.amazonaws.saas.eks.customer.model.search.CustomerSearchResponse;
import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.repository.AccountRepository;
import com.amazonaws.saas.eks.service.CustomerService;
import org.springframework.stereotype.Service;

import com.amazonaws.saas.eks.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService {
	private final CustomerRepository customerRepository;
	private final AccountRepository accountRepository;

	private static final int DEFAULT_SEARCH_START = 0;
	private static final int DEFAULT_SEARCH_SIZE = 10;

	public CustomerServiceImpl(CustomerRepository customerRepository,
							   AccountRepository accountRepository) {
		this.customerRepository = customerRepository;
		this.accountRepository = accountRepository;
	}

	@Override
	public CustomerResponse create(CreateCustomerRequest request, String tenantId) {
		Customer customer = CustomerMapper.INSTANCE.createCustomerRequestToCustomer(request);
		Customer model = customerRepository.create(customer, tenantId);
		buildMainCustomerAccount(tenantId, model);
		return CustomerMapper.INSTANCE.customerToCustomerResponse(model);
	}

	@Override
	public CustomerResponse read(String customerId, String tenantId) {
		Customer customer = customerRepository.getById(customerId, tenantId);
		if (customer == null) {
			throw new EntityNotFoundException(EntityType.CUSTOMERS.getLabel(), tenantId);
		}
		return CustomerMapper.INSTANCE.customerToCustomerResponse(customer);
	}

	@Override
	public CustomerResponse update(UpdateCustomerRequest request, String customerId, String tenantId) {
		Customer customer = CustomerMapper.INSTANCE.updateCustomerRequestToCustomer(request);
		customer.setId(customerId);
		Customer updatedCustomer = customerRepository.update(customer, tenantId);
		return CustomerMapper.INSTANCE.customerToCustomerResponse(updatedCustomer);
	}

	@Override
	public void delete(String customerId, String tenantId) {
		customerRepository.delete(customerId, tenantId);
	}

	@Override
	public ListCustomersResponse search(CustomerSearchRequest request, String tenantId) {
		int from = request.getFrom() == null ? DEFAULT_SEARCH_START : request.getFrom();
		int size = request.getSize() == null ? DEFAULT_SEARCH_SIZE : request.getSize();
		CustomerSearchResponse searchResponse = customerRepository.search(tenantId, from, size,
				request.getFilter(), request.getSortBy());
		return CustomerMapper.INSTANCE.customerSearchResponseToListCustomerResponse(searchResponse);
	}

	@Override
	public ListAccountsResponse searchCustomerAccounts(AccountSearchRequest request, String customerId, String tenantId) {
		int from = request.getFrom() == null ? DEFAULT_SEARCH_START : request.getFrom();
		int size = request.getSize() == null ? DEFAULT_SEARCH_SIZE : request.getSize();
		AccountSearchResponse searchResponse = accountRepository.search(tenantId, from, size,
				request.getFilter(), request.getSortBy(), customerId);
		return CustomerMapper.INSTANCE.accountSearchResponseToListAccountsResponse(searchResponse);
	}

	private void buildMainCustomerAccount(String tenantId, Customer model) {
		Account mainAccount = CustomerMapper.INSTANCE.customerToAccount(model);
		mainAccount.setIsMain(true);
		mainAccount.setCustomerId(model.getId());
		accountRepository.create(mainAccount, tenantId);
	}
}

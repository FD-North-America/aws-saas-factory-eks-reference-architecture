package com.amazonaws.saas.eks.service.impl;


import com.amazonaws.saas.eks.customer.dto.requests.CreateCustomerRequest;
import com.amazonaws.saas.eks.customer.dto.responses.CustomerResponse;
import com.amazonaws.saas.eks.customer.mapper.CustomerMapper;
import com.amazonaws.saas.eks.customer.model.Customer;
import com.amazonaws.saas.eks.service.CustomerService;
import org.springframework.stereotype.Service;

import com.amazonaws.saas.eks.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService {
	private final CustomerRepository customerRepository;

	public CustomerServiceImpl(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Override
	public CustomerResponse create(CreateCustomerRequest request, String tenantId) {
		Customer customer = CustomerMapper.INSTANCE.createCustomerRequestToCustomer(request);
		Customer model = customerRepository.create(customer, tenantId);
		return CustomerMapper.INSTANCE.customerToCustomerResponse(model);
	}
}

package com.amazonaws.saas.eks.service.impl;


import com.amazonaws.saas.eks.service.CustomerService;
import org.springframework.stereotype.Service;

import com.amazonaws.saas.eks.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService {
	private CustomerRepository customerRepository;

	public CustomerServiceImpl(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}
}

package com.amazonaws.saas.eks.controller;


import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.customer.dto.requests.CreateCustomerRequest;
import com.amazonaws.saas.eks.customer.dto.responses.CustomerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.amazonaws.saas.eks.service.CustomerService;

import javax.validation.Valid;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class CustomerController {
	private static final Logger logger = LogManager.getLogger(CustomerController.class);

	@Autowired
	private CustomerService customerService;

	@Autowired
	private JwtAuthManager jwtAuthManager;

	@PostMapping(value = "{tenantId}/customers", produces = { MediaType.APPLICATION_JSON_VALUE })
	public CustomerResponse create(@RequestBody @Valid CreateCustomerRequest request) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return customerService.create(request, tenantId);
		} catch (Exception e) {
			logger.error(String.format("Error creating customer. TenantId: %s", tenantId), e);
			throw e;
		}
	}

	/**
	 * Heartbeat method to check if customer service is up and running
	 *
	 */
	@RequestMapping("{tenantId}/customers/health")
	public String health() {
		return "\"Customer service is up!\"";
	}
}

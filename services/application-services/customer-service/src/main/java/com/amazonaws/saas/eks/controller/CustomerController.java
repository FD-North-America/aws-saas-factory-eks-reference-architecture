package com.amazonaws.saas.eks.controller;


import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.customer.dto.requests.AccountSearchRequest;
import com.amazonaws.saas.eks.customer.dto.requests.CreateCustomerRequest;
import com.amazonaws.saas.eks.customer.dto.requests.CustomerSearchRequest;
import com.amazonaws.saas.eks.customer.dto.requests.UpdateCustomerRequest;
import com.amazonaws.saas.eks.customer.dto.responses.CustomerResponse;
import com.amazonaws.saas.eks.customer.dto.responses.ListAccountsResponse;
import com.amazonaws.saas.eks.customer.dto.responses.ListCustomersResponse;
import com.amazonaws.saas.eks.service.CustomerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class CustomerController {
	private static final Logger logger = LogManager.getLogger(CustomerController.class);

	@Autowired
	private CustomerService customerService;

	@Autowired
	private JwtAuthManager jwtAuthManager;

	@GetMapping(value = "{tenantId}/customers/{customerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public CustomerResponse get(@PathVariable String customerId) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return customerService.read(customerId, tenantId);
		} catch (Exception e) {
			logger.error(String.format("error reading customer. CustomerId: %s, TenantId: %s", customerId, tenantId), e);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/customers", produces = { MediaType.APPLICATION_JSON_VALUE })
	public CustomerResponse create(@RequestBody @Valid CreateCustomerRequest request) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return customerService.create(request, tenantId);
		} catch (Exception e) {
			logger.error(String.format("error creating customer. TenantId: %s", tenantId), e);
			throw e;
		}
	}

	@PutMapping(value = "{tenantId}/customers/{customerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public CustomerResponse update(@RequestBody @Valid UpdateCustomerRequest request,
								   @PathVariable String customerId) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return customerService.update(request, customerId, tenantId);
		} catch (Exception e) {
			logger.error(String.format("error updating customer. CustomerId: %s, TenantId: %s", customerId, tenantId), e);
			throw e;
		}
	}

	@DeleteMapping(value = "{tenantId}/customers/{customerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public void delete(@PathVariable String customerId) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			customerService.delete(customerId, tenantId);
		} catch (Exception e) {
			logger.error(String.format("error deleting customer. CustomerId: %s, TenantId: %s", customerId, tenantId), e);
			throw e;
		}
	}

	@GetMapping(value = "{tenantId}/customers", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ListCustomersResponse search(@Valid CustomerSearchRequest request) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return customerService.search(request, tenantId);
		} catch (Exception e) {
			logger.error("error searching customers", e);
			throw e;
		}
	}

	@GetMapping(value = "{tenantId}/customers/{customerId}/accounts", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ListAccountsResponse searchCustomerAccounts(@Valid AccountSearchRequest request,
													   @PathVariable String customerId) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return customerService.searchCustomerAccounts(request, customerId, tenantId);
		} catch (Exception e) {
			logger.error("error searching customer accounts", e);
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

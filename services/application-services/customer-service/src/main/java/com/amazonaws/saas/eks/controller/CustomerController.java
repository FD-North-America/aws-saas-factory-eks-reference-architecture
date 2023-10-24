package com.amazonaws.saas.eks.controller;


import com.amazonaws.saas.eks.auth.JwtAuthManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.amazonaws.saas.eks.service.CustomerService;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class CustomerController {
	private static final Logger logger = LogManager.getLogger(CustomerController.class);

	@Autowired
	private CustomerService customerService;

	@Autowired
	private JwtAuthManager jwtAuthManager;

	/**
	 * Heartbeat method to check if customer service is up and running
	 *
	 */
	@RequestMapping("{tenantId}/customers/health")
	public String health() {
		return "\"Customer service is up!\"";
	}
}

package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.service.ReportService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ReportController {
	private static final Logger logger = LogManager.getLogger(ReportController.class);

	@Autowired
	private ReportService reportService;

	@Autowired
	private JwtAuthManager jwtAuthManager;

	/**
	 * Heartbeat method to check if payment service is up and running
	 *
	 */
	@GetMapping(value = "{tenantId}/reports/health")
	public String health() {
		return "\"Report service is up!\"";
	}
}

package com.amazonaws.saas.eks.controller;


import com.amazonaws.saas.eks.auth.JwtAuthManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class SettingsController {
	private static final Logger logger = LogManager.getLogger(SettingsController.class);

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private JwtAuthManager jwtAuthManager;

	/**
	 * Heartbeat method to check if settings service is up and running
	 *
	 */
	@RequestMapping("{tenantId}/settings/health")
	public String health() {
		return "\"Settings service is up!\"";
	}
}

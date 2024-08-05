package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.service.ReasonCodesSettingsService;
import com.amazonaws.saas.eks.settings.dto.requests.reasoncodes.CreateReasonCodesSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.reasoncodes.UpdateReasonCodesSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.ReasonCodesSettingsResponse;
import com.amazonaws.saas.eks.settings.model.v2.Permission;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ReasonCodesSettingsController {
	private static final Logger logger = LogManager.getLogger(ReasonCodesSettingsController.class);

	@Autowired
	private ReasonCodesSettingsService settingsService;

	@Autowired
	private JwtAuthManager jwtAuthManager;

//	@PreAuthorize("hasAnyAuthority('" + Permission.REASON_CODES_SETTINGS_CREATE + "')")
	@PostMapping(value = "{tenantId}/settings/reason-codes", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ReasonCodesSettingsResponse create(@RequestBody @Valid CreateReasonCodesSettingsRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return settingsService.create(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating Reason Codes Settings", e);
			throw e;
		}
	}

//	@PreAuthorize("hasAnyAuthority('" + Permission.REASON_CODES_SETTINGS_READ + "')")
	@GetMapping(value = "{tenantId}/settings/reason-codes", produces = {MediaType.APPLICATION_JSON_VALUE })
	public ReasonCodesSettingsResponse get(@PathVariable String tenantId) {
		try {
			return settingsService.get(tenantId);
		} catch (Exception e) {
			logger.error("Reason Codes Settings not found", e);
			throw e;
		}
	}

//	@PreAuthorize("hasAnyAuthority('" + Permission.REASON_CODES_SETTINGS_UPDATE + "')")
	@PutMapping(value = "{tenantId}/settings/reason-codes", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ReasonCodesSettingsResponse update(@RequestBody @Valid UpdateReasonCodesSettingsRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return settingsService.update(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error updating Reason Codes Settings", e);
			throw e;
		}
	}

//	@PreAuthorize("hasAnyAuthority('" + Permission.REASON_CODES_SETTINGS_DELETE + "')")
	@DeleteMapping(value = "{tenantId}/settings/reason-codes/{code}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public void delete(@PathVariable String code) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			settingsService.delete(tu.getTenantId(), code);
		} catch (Exception e) {
			logger.error("Error deleting Reason Codes Settings", e);
			throw e;
		}
	}
}

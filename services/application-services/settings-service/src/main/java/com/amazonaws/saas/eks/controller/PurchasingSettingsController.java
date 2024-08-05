package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.service.PurchasingSettingsService;
import com.amazonaws.saas.eks.settings.dto.requests.purchasing.CreatePurchasingSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.purchasing.UpdatePurchasingSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.PurchasingSettingsResponse;
import com.amazonaws.saas.eks.settings.model.v2.Permission;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class PurchasingSettingsController {
	private static final Logger logger = LogManager.getLogger(PurchasingSettingsController.class);

	private final PurchasingSettingsService settingsService;

	private final JwtAuthManager jwtAuthManager;

	public PurchasingSettingsController(PurchasingSettingsService settingsService, JwtAuthManager jwtAuthManager) {
		this.settingsService = settingsService;
		this.jwtAuthManager = jwtAuthManager;
	}

//	@PreAuthorize("hasAnyAuthority('" + Permission.PURCHASING_SETTINGS_CREATE + "')")
	@PostMapping(value = "{tenantId}/settings/purchasing", produces = { MediaType.APPLICATION_JSON_VALUE })
	public PurchasingSettingsResponse create(@PathVariable String tenantId,
											 @RequestBody @Valid CreatePurchasingSettingsRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return settingsService.create(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating Purchasing Settings", e);
			throw e;
		}
	}

//	@PreAuthorize("hasAnyAuthority('" + Permission.PURCHASING_SETTINGS_READ + "')")
	@GetMapping(value = "{tenantId}/settings/purchasing", produces = {MediaType.APPLICATION_JSON_VALUE })
	public PurchasingSettingsResponse get(@PathVariable String tenantId) {
		try {
			return settingsService.get(tenantId);
		} catch (Exception e) {
			logger.error("Purchasing Settings not found", e);
			throw e;
		}
	}

//	@PreAuthorize("hasAnyAuthority('" + Permission.PURCHASING_SETTINGS_UPDATE + "')")
	@PutMapping(value = "{tenantId}/settings/purchasing", produces = {MediaType.APPLICATION_JSON_VALUE})
	public PurchasingSettingsResponse update(@PathVariable String tenantId,
											 @RequestBody @Valid UpdatePurchasingSettingsRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return settingsService.update(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error updating Purchasing Settings", e);
			throw e;
		}
	}

//	@PreAuthorize("hasAnyAuthority('" + Permission.PURCHASING_SETTINGS_DELETE + "')")
	@DeleteMapping(value = "{tenantId}/settings/purchasing", produces = {MediaType.APPLICATION_JSON_VALUE})
	public void delete(@PathVariable String tenantId) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			settingsService.delete(tu.getTenantId());
		} catch (Exception e) {
			logger.error("Error deleting Purchasing Settings", e);
			throw e;
		}
	}
}

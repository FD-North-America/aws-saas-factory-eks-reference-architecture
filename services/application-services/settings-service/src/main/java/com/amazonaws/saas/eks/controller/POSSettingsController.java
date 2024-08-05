package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.service.POSSettingsService;
import com.amazonaws.saas.eks.settings.dto.requests.pos.CreatePOSSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.pos.UpdatePOSSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.POSSettingsResponse;
import com.amazonaws.saas.eks.settings.model.enums.SequenceNumberType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class POSSettingsController {
	private static final Logger logger = LogManager.getLogger(POSSettingsController.class);

	@Autowired
	private POSSettingsService settingsService;

	@Autowired
	private JwtAuthManager jwtAuthManager;

//	@PreAuthorize("hasAnyAuthority('" + Permission.POS_SETTINGS_CREATE + "')")
	@PostMapping(value = "{tenantId}/settings/pos", produces = { MediaType.APPLICATION_JSON_VALUE })
	public POSSettingsResponse create(@RequestBody @Valid CreatePOSSettingsRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return settingsService.create(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating POS Settings", e);
			throw e;
		}
	}

//	@PreAuthorize("hasAnyAuthority('" + Permission.POS_SETTINGS_READ + "')")
	@GetMapping(value = "{tenantId}/settings/pos", produces = {MediaType.APPLICATION_JSON_VALUE })
	public POSSettingsResponse get(@PathVariable String tenantId) {
		try {
			return settingsService.get(tenantId);
		} catch (Exception e) {
			logger.error("POS Settings not found", e);
			throw e;
		}
	}

//	@PreAuthorize("hasAnyAuthority('" + Permission.POS_SETTINGS_UPDATE + "')")
	@PutMapping(value = "{tenantId}/settings/pos", produces = {MediaType.APPLICATION_JSON_VALUE})
	public POSSettingsResponse update(@RequestBody @Valid UpdatePOSSettingsRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return settingsService.update(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error updating POS Settings", e);
			throw e;
		}
	}

//	@PreAuthorize("hasAnyAuthority('" + Permission.POS_SETTINGS_DELETE + "')")
	@DeleteMapping(value = "{tenantId}/settings/pos", produces = {MediaType.APPLICATION_JSON_VALUE})
	public void delete() {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			settingsService.delete(tu.getTenantId());
		} catch (Exception e) {
			logger.error("Error deleting POS Settings", e);
			throw e;
		}
	}

	@GetMapping(value = "{tenantId}/settings/pos/{type}/next-sequence", produces = {MediaType.APPLICATION_JSON_VALUE})
	public String getNextSequence(@PathVariable String tenantId,
								  @PathVariable String type) {
		try {
			SequenceNumberType t = SequenceNumberType.valueOfLabel(type);
			if (t == null) return "";
			return settingsService.getNextSequence(tenantId, t);
		} catch (Exception e) {
			logger.error(String.format("Error getting next sequence for %s type - POS Settings", type), e);
			throw e;
		}
	}

	/**
	 * Heartbeat method to check if settings service is up and running
	 *
	 */
	@RequestMapping("{tenantId}/settings/health")
	public String health() {
		return "\"Settings service is up!\"";
	}
}

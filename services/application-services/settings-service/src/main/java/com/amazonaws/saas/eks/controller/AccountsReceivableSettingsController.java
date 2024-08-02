package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.service.AccountsReceivableSettingsService;
import com.amazonaws.saas.eks.settings.dto.requests.accountsreceivable.CreateAccountsReceivableSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.accountsreceivable.UpdateAccountsReceivableSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.AccountsReceivableSettingsResponse;
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
public class AccountsReceivableSettingsController {
	private static final Logger logger = LogManager.getLogger(AccountsReceivableSettingsController.class);

	@Autowired
	private AccountsReceivableSettingsService settingsService;

	@Autowired
	private JwtAuthManager jwtAuthManager;

//	@PreAuthorize("hasAnyAuthority('" + Permission.ACCOUNTS_RECEIVABLE_SETTINGS_CREATE + "')")
	@PostMapping(value = "{tenantId}/settings/accounts-receivable", produces = { MediaType.APPLICATION_JSON_VALUE })
	public AccountsReceivableSettingsResponse create(@RequestBody @Valid CreateAccountsReceivableSettingsRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return settingsService.create(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error creating Accounts Receivable settings", e);
			throw e;
		}
	}

//	@PreAuthorize("hasAnyAuthority('" + Permission.ACCOUNTS_RECEIVABLE_SETTINGS_READ + "')")
	@GetMapping(value = "{tenantId}/settings/accounts-receivable", produces = {MediaType.APPLICATION_JSON_VALUE })
	public AccountsReceivableSettingsResponse get(@PathVariable String tenantId) {
		try {
			return settingsService.get(tenantId);
		} catch (Exception e) {
			logger.error("Accounts Receivable settings not found", e);
			throw e;
		}
	}

//	@PreAuthorize("hasAnyAuthority('" + Permission.ACCOUNTS_RECEIVABLE_SETTINGS_UPDATE + "')")
	@PutMapping(value = "{tenantId}/settings/accounts-receivable", produces = {MediaType.APPLICATION_JSON_VALUE})
	public AccountsReceivableSettingsResponse update(@RequestBody @Valid UpdateAccountsReceivableSettingsRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return settingsService.update(tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error updating Accounts Receivable settings", e);
			throw e;
		}
	}

//	@PreAuthorize("hasAnyAuthority('" + Permission.ACCOUNTS_RECEIVABLE_SETTINGS_DELETE + "')")
	@DeleteMapping(value = "{tenantId}/settings/accounts-receivable", produces = {MediaType.APPLICATION_JSON_VALUE})
	public void delete() {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			settingsService.delete(tu.getTenantId());
		} catch (Exception e) {
			logger.error("Error deleting Accounts Receivable settings", e);
			throw e;
		}
	}
}

package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.service.SalesTaxSettingsService;
import com.amazonaws.saas.eks.settings.dto.requests.salestax.CreateSalesTaxSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.salestax.ListSalesTaxSettingsRequestParams;
import com.amazonaws.saas.eks.settings.dto.requests.salestax.UpdateSalesTaxSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.ListSalesTaxSettingsResponse;
import com.amazonaws.saas.eks.settings.dto.responses.SalesTaxSettingsResponse;
import com.amazonaws.saas.eks.settings.model.v2.Permission;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class SalesTaxSettingsController {
    private static final Logger logger = LogManager.getLogger(SalesTaxSettingsController.class);

    @Autowired
    private SalesTaxSettingsService settingsService;

    @Autowired
    private JwtAuthManager jwtAuthManager;

//    @PreAuthorize("hasAnyAuthority('" + Permission.SALES_TAX_SETTINGS_CREATE + "')")
    @PostMapping(value = "{tenantId}/settings/sales-tax", produces = { MediaType.APPLICATION_JSON_VALUE })
    public SalesTaxSettingsResponse create(@RequestBody @Valid CreateSalesTaxSettingsRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return settingsService.create(tu.getTenantId(), request);
        } catch (Exception e) {
            logger.error("Error creating Sales Tax Settings", e);
            throw e;
        }
    }

//    @PreAuthorize("hasAnyAuthority('" + Permission.SALES_TAX_SETTINGS_READ + "')")
    @GetMapping(value = "{tenantId}/settings/sales-tax/{salesTaxSettingsId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public SalesTaxSettingsResponse get(@PathVariable("salesTaxSettingsId") String salesTaxSettingsId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return settingsService.get(tu.getTenantId(), salesTaxSettingsId);
        } catch (Exception e) {
            logger.error(String.format("Sales Tax Settings not found with ID: %s", salesTaxSettingsId), e);
            throw e;
        }
    }

//    @PreAuthorize("hasAnyAuthority('" + Permission.SALES_TAX_SETTINGS_READ + "')")
    @GetMapping(value = "{tenantId}/settings/sales-tax", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ListSalesTaxSettingsResponse getAll(@PathVariable("tenantId") String tenantId,
                                               ListSalesTaxSettingsRequestParams params) {
        try {
            return settingsService.getAll(tenantId, params);
        } catch (Exception e) {
            logger.error("Error listing Sales Tax Settings", e);
            throw e;
        }
    }

//    @PreAuthorize("hasAnyAuthority('" + Permission.SALES_TAX_SETTINGS_UPDATE + "')")
    @PutMapping(value = "{tenantId}/settings/sales-tax/{salesTaxSettingsId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public SalesTaxSettingsResponse update(@PathVariable("salesTaxSettingsId") String salesTaxSettingsId,
                                           @RequestBody @Valid UpdateSalesTaxSettingsRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return settingsService.update(tu.getTenantId(), salesTaxSettingsId, request);
        } catch (Exception e) {
            logger.error("Error updating Sales Tax Settings", e);
            throw e;
        }
    }

//    @PreAuthorize("hasAnyAuthority('" + Permission.SALES_TAX_SETTINGS_DELETE + "')")
    @DeleteMapping(value = "{tenantId}/settings/sales-tax/{salesTaxSettingsId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public void delete(@PathVariable("salesTaxSettingsId") String salesTaxSettingsId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            settingsService.delete(tu.getTenantId(), salesTaxSettingsId);
        } catch (Exception e) {
            logger.error("Error deleting Sales Tax Settings", e);
            throw e;
        }
    }
}

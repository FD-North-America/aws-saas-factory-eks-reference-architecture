package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.service.InventorySettingsService;
import com.amazonaws.saas.eks.settings.dto.requests.inventory.CreateInventorySettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.inventory.UpdateInventorySettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.pos.UpdatePOSSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.InventorySettingsResponse;
import com.amazonaws.saas.eks.settings.dto.responses.POSSettingsResponse;
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
public class InventorySettingsController {
    private static final Logger logger = LogManager.getLogger(InventorySettingsController.class);

    @Autowired
    private InventorySettingsService settingsService;

    @Autowired
    private JwtAuthManager jwtAuthManager;

//    @PreAuthorize("hasAnyAuthority('" + Permission.INVENTORY_SETTINGS_CREATE + "')")
    @PostMapping(value = "{tenantId}/settings/inventory", produces = { MediaType.APPLICATION_JSON_VALUE })
    public InventorySettingsResponse create(@RequestBody @Valid CreateInventorySettingsRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return settingsService.create(tu.getTenantId(), request);
        } catch (Exception e) {
            logger.error("Error creating Inventory Settings", e);
            throw e;
        }
    }

//    @PreAuthorize("hasAnyAuthority('" + Permission.INVENTORY_SETTINGS_READ + "')")
    @GetMapping(value = "{tenantId}/settings/inventory", produces = {MediaType.APPLICATION_JSON_VALUE })
    public InventorySettingsResponse get(@PathVariable String tenantId) {
        try {
            return settingsService.get(tenantId);
        } catch (Exception e) {
            logger.error("Inventory Settings not found", e);
            throw e;
        }
    }

//    @PreAuthorize("hasAnyAuthority('" + Permission.INVENTORY_SETTINGS_UPDATE + "')")
    @PutMapping(value = "{tenantId}/settings/inventory", produces = {MediaType.APPLICATION_JSON_VALUE})
    public InventorySettingsResponse update(@RequestBody @Valid UpdateInventorySettingsRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return settingsService.update(tu.getTenantId(), request);
        } catch (Exception e) {
            logger.error("Error updating Inventory Settings", e);
            throw e;
        }
    }

//    @PreAuthorize("hasAnyAuthority('" + Permission.INVENTORY_SETTINGS_DELETE + "')")
    @DeleteMapping(value = "{tenantId}/settings/inventory", produces = {MediaType.APPLICATION_JSON_VALUE})
    public void delete() {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            settingsService.delete(tu.getTenantId());
        } catch (Exception e) {
            logger.error("Error deleting Inventory Settings", e);
            throw e;
        }
    }
}

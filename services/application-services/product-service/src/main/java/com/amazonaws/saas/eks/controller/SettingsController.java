package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.dto.responses.settings.SettingsResponse;
import com.amazonaws.saas.eks.service.SettingsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class SettingsController {
    private static final Logger logger = LogManager.getLogger(SettingsController.class);

    @Autowired
    private JwtAuthManager jwtAuthManager;

    @Autowired
    private SettingsService settingsService;

    @GetMapping(value = "{tenantId}/products/settings", produces = {MediaType.APPLICATION_JSON_VALUE})
    public SettingsResponse get() {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return settingsService.get(tu.getTenantId());
        } catch (Exception e) {
            logger.error("Error getting settings", e);
            throw e;
        }
    }
}

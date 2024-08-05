package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.payment.dto.requests.gateway.AuthRequest;
import com.amazonaws.saas.eks.payment.model.Permission;
import com.amazonaws.saas.eks.service.GatewayService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class GatewayController {
    private static final Logger logger = LogManager.getLogger(GatewayController.class);

    @Autowired
    private GatewayService gatewayService;

    @Autowired
    private JwtAuthManager jwtAuthManager;

    @PreAuthorize("hasAnyAuthority('" + Permission.POS_UPDATE + "','" + Permission.POS_SERVER_UPDATE + "')")
    @PostMapping(value = "{tenantId}/payments/gateway/auth", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> auth(@Valid @RequestBody AuthRequest request) {
        TenantUser tu = jwtAuthManager.getTenantUser();
        try {
            return gatewayService.auth(tu.getTenantId(), request);
        } catch (Exception e) {
            logger.error("error on auth", e);
            throw e;
        }
    }
}

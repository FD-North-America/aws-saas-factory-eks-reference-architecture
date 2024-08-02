package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.product.dto.requests.uom.CreateUOMRequest;
import com.amazonaws.saas.eks.product.dto.requests.uom.ListUOMRequestParams;
import com.amazonaws.saas.eks.product.dto.requests.uom.UpdateUOMRequest;
import com.amazonaws.saas.eks.product.dto.responses.uom.ListUOMResponse;
import com.amazonaws.saas.eks.product.dto.responses.uom.UOMResponse;
import com.amazonaws.saas.eks.product.model.Permission;
import com.amazonaws.saas.eks.service.UOMService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class UOMController {
    private static final Logger logger = LogManager.getLogger(UOMController.class);

    @Autowired
    private JwtAuthManager jwtAuthManager;

    @Autowired
    private UOMService uomService;

    @PreAuthorize("hasAnyAuthority('" + Permission.PRODUCT_CREATE + "')")
    @PostMapping(value = "{tenantId}/products/uom", produces = {MediaType.APPLICATION_JSON_VALUE})
    public UOMResponse create(@RequestBody @Valid CreateUOMRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return uomService.create(tu.getTenantId(), request);
        } catch (Exception e) {
            logger.error("Error creating UOM", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.PRODUCT_READ + "')")
    @GetMapping(value = "{tenantId}/products/uom", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ListUOMResponse getAll(@Valid ListUOMRequestParams params) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return uomService.getAll(tu.getTenantId(), params);
        } catch (Exception e) {
            logger.error("Error listing uom", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.PRODUCT_READ + "')")
    @GetMapping(value = "{tenantId}/products/uom/{uomId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public UOMResponse get(@PathVariable("uomId") String uomId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return uomService.get(tu.getTenantId(), uomId);
        } catch (Exception e) {
            logger.error(String.format("UOM not found with ID: %s", uomId), e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.PRODUCT_UPDATE + "')")
    @PutMapping(value = "{tenantId}/products/uom/{uomId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public UOMResponse update(@PathVariable("uomId") String uomId,
                              @RequestBody @Valid UpdateUOMRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return uomService.update(tu.getTenantId(), uomId, request);
        } catch (Exception e) {
            logger.error("Error updating UOM", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.PRODUCT_DELETE + "')")
    @DeleteMapping(value = "{tenantId}/products/uom/{uomId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public void delete(@PathVariable("uomId") String uomId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            uomService.delete(tu.getTenantId(), uomId);
        } catch (Exception e) {
            logger.error("Error deleting UOM", e);
            throw e;
        }
    }
}

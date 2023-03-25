package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.dto.requests.product.ListProductRequestParams;
import com.amazonaws.saas.eks.dto.requests.volumepricing.CreateVolumePricingRequest;
import com.amazonaws.saas.eks.dto.requests.volumepricing.ListVolumePricingRequestParams;
import com.amazonaws.saas.eks.dto.requests.volumepricing.UpdateVolumePricingRequest;
import com.amazonaws.saas.eks.dto.responses.product.ListProductResponse;
import com.amazonaws.saas.eks.dto.responses.volumepricing.ListVolumePricingResponse;
import com.amazonaws.saas.eks.dto.responses.volumepricing.VolumePricingResponse;
import com.amazonaws.saas.eks.model.Permission;
import com.amazonaws.saas.eks.service.VolumePricingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class VolumePricingController {
    private static final Logger logger = LogManager.getLogger(VolumePricingController.class);

    @Autowired
    private JwtAuthManager jwtAuthManager;

    @Autowired
    private VolumePricingService volumePricingService;

    @PreAuthorize("hasAnyAuthority('" + Permission.VOLUME_DISCOUNT_CREATE + "')")
    @PostMapping(value = "{tenantId}/products/volume-pricing", produces = {MediaType.APPLICATION_JSON_VALUE})
    public VolumePricingResponse create(@RequestBody @Valid CreateVolumePricingRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return volumePricingService.create(tu.getTenantId(), request);
        } catch (Exception e) {
            logger.error("Error creating VolumePricing", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.VOLUME_DISCOUNT_READ + "')")
    @GetMapping(value = "{tenantId}/products/volume-pricing", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ListVolumePricingResponse getAll(@Valid ListVolumePricingRequestParams params) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return volumePricingService.getAll(tu.getTenantId(), params);
        } catch (Exception e) {
            logger.error("Error listing VolumePricing", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.VOLUME_DISCOUNT_READ + "')")
    @GetMapping(value = "{tenantId}/products/volume-pricing/{volumePricingId}",
            produces = {MediaType.APPLICATION_JSON_VALUE })
    public VolumePricingResponse get(@PathVariable("volumePricingId") String volumePricingId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return volumePricingService.get(tu.getTenantId(), volumePricingId);
        } catch (Exception e) {
            logger.error(String.format("VolumePricing not found with ID: %s", volumePricingId), e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.VOLUME_DISCOUNT_UPDATE + "')")
    @PutMapping(value = "{tenantId}/products/volume-pricing/{volumePricingId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public VolumePricingResponse update(@PathVariable("volumePricingId") String volumePricingId,
                                        @RequestBody @Valid UpdateVolumePricingRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return volumePricingService.update(tu.getTenantId(), volumePricingId, request);
        } catch (Exception e) {
            logger.error("Error updating VolumePricing", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.VOLUME_DISCOUNT_DELETE + "')")
    @DeleteMapping(value = "{tenantId}/products/volume-pricing/{volumePricingId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public void delete(@PathVariable("volumePricingId") String volumePricingId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            volumePricingService.delete(tu.getTenantId(), volumePricingId);
        } catch (Exception e) {
            logger.error("Error deleting VolumePricing", e);
            throw e;
        }
    }
}

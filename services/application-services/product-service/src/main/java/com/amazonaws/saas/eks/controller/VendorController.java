package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.dto.requests.vendor.CreateVendorRequest;
import com.amazonaws.saas.eks.dto.requests.vendor.ListVendorsRequestParams;
import com.amazonaws.saas.eks.dto.requests.vendor.UpdateVendorRequest;
import com.amazonaws.saas.eks.dto.responses.vendor.ListVendorResponse;
import com.amazonaws.saas.eks.dto.responses.vendor.VendorResponse;
import com.amazonaws.saas.eks.model.Permission;
import com.amazonaws.saas.eks.service.VendorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class VendorController {
    private static final Logger logger = LogManager.getLogger(VendorController.class);

    @Autowired
    private JwtAuthManager jwtAuthManager;

    @Autowired
    private VendorService vendorService;

    @PreAuthorize("hasAnyAuthority('" + Permission.VENDOR_CREATE + "')")
    @PostMapping(value = "{tenantId}/products/vendors", produces = {MediaType.APPLICATION_JSON_VALUE})
    public VendorResponse create(@RequestBody @Valid CreateVendorRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return vendorService.create(tu.getTenantId(), request);
        } catch (Exception e) {
            logger.error("Error creating vendor", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.VENDOR_READ + "')")
    @GetMapping(value = "{tenantId}/products/vendors/{vendorId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public VendorResponse get(@PathVariable("vendorId") String vendorId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return vendorService.get(tu.getTenantId(), vendorId);
        } catch (Exception e) {
            logger.error("Vendor not found with ID: " + vendorId, e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.VENDOR_UPDATE + "')")
    @PutMapping(value = "{tenantId}/products/vendors/{vendorId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public VendorResponse update(@PathVariable("vendorId") String vendorId,
                                 @RequestBody @Valid UpdateVendorRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return vendorService.update(tu.getTenantId(), vendorId, request);
        } catch (Exception e) {
            logger.error("Error updating vendor", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.VENDOR_DELETE + "')")
    @DeleteMapping(value = "{tenantId}/products/vendors/{vendorId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public void delete(@PathVariable("vendorId") String vendorId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            vendorService.delete(tu.getTenantId(), vendorId);
        } catch (Exception e) {
            logger.error("Error deleting vendor", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.VENDOR_READ + "')")
    @GetMapping(value = "{tenantId}/products/vendors", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ListVendorResponse getAll(@Valid ListVendorsRequestParams params) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return vendorService.getAll(tu.getTenantId(), params);
        } catch (Exception e) {
            logger.error("Vendors not found", e);
            throw e;
        }
    }
}

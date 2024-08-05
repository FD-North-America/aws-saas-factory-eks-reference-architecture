package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.workstations.CreateWorkstationRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.workstations.UpdateWorkstationRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.workstations.ListWorkstationsResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.workstations.WorkstationResponse;
import com.amazonaws.saas.eks.cashdrawer.model.Permission;
import com.amazonaws.saas.eks.service.WorkstationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class WorkstationController {
    private static final Logger logger = LogManager.getLogger(WorkstationController.class);

    private final WorkstationService service;
    private final JwtAuthManager jwtAuthManager;

    public WorkstationController(WorkstationService service, JwtAuthManager jwtAuthManager) {
        this.service = service;
        this.jwtAuthManager = jwtAuthManager;
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_CREATE + "', '" + Permission.CASH_DRAWER_CHECKOUT_CLERK_CREATE + "')")
    @PostMapping(value = "{tenantId}/workstations", produces = { MediaType.APPLICATION_JSON_VALUE })
    public WorkstationResponse create(@RequestBody @Valid CreateWorkstationRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return service.create(request, tu.getTenantId());
        } catch (Exception e) {
            logger.error("Error creating workstation", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_READ + "', '" + Permission.SERVER_CASH_DRAWER_READ + "', '" + Permission.CASH_DRAWER_CHECKOUT_CLERK_READ + "')")
    @GetMapping(value = "{tenantId}/workstations/{workstationId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public WorkstationResponse get(@PathVariable("workstationId") String workstationId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return service.get(workstationId, tu.getTenantId());
        } catch (Exception e) {
            logger.error(String.format("Workstation not found with ID: %s", workstationId), e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_UPDATE + "', '" + Permission.CASH_DRAWER_CHECKOUT_CLERK_UPDATE + "')")
    @PutMapping(value = "{tenantId}/workstations/{workstationId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public WorkstationResponse update(@PathVariable("workstationId") String workstationId,
                                      @RequestBody @Valid UpdateWorkstationRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return service.update(workstationId, request, tu.getTenantId());
        } catch (Exception e) {
            logger.error(String.format("Error updating workstation: %s", workstationId), e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_DELETE + "', '" + Permission.CASH_DRAWER_CHECKOUT_CLERK_DELETE + "')")
    @DeleteMapping(value = "{tenantId}/workstations/{workstationId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public void delete(@PathVariable String workstationId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            service.delete(workstationId, tu.getTenantId());
        } catch (Exception e) {
            logger.error(String.format("Error deleting workstation: %s", workstationId), e);
            throw e;
        }
    }

    @GetMapping(value = "{tenantId}/workstations", produces = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_READ + "', '" + Permission.SERVER_CASH_DRAWER_READ + "', '" + Permission.CASH_DRAWER_CHECKOUT_CLERK_READ + "')")
    public ListWorkstationsResponse getAll() {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return service.getAll(tu.getTenantId());
        } catch (Exception e) {
            logger.error("Error listing workstations", e);
            throw e;
        }
    }
}

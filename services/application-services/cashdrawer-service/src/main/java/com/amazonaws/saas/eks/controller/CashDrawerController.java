package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.CreateCashDrawerRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.ListCashDrawersRequestParams;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.UpdateCashDrawerRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.CashDrawerResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.CheckoutDetailsResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.ListCashDrawersResponse;
import com.amazonaws.saas.eks.cashdrawer.model.Permission;
import com.amazonaws.saas.eks.service.CashDrawerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class CashDrawerController {
    private static final Logger logger = LogManager.getLogger(CashDrawerController.class);

    @Autowired
    private CashDrawerService cashDrawerService;

    @Autowired
    private JwtAuthManager jwtAuthManager;

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_CREATE + "', '" + Permission.CASH_DRAWER_CHECKOUT_CLERK_CREATE + "')")
    @PostMapping(value = "{tenantId}/cashdrawers", produces = { MediaType.APPLICATION_JSON_VALUE })
    public CashDrawerResponse create(@RequestBody @Valid CreateCashDrawerRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return cashDrawerService.create(request, tu.getTenantId());
        } catch (Exception e) {
            logger.error("Error creating cash drawer", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_READ + "', '" + Permission.SERVER_CASH_DRAWER_READ + "', '" + Permission.CASH_DRAWER_CHECKOUT_CLERK_READ + "')")
    @GetMapping(value = "{tenantId}/cashdrawers/{cashDrawerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public CashDrawerResponse get(@PathVariable("tenantId") String tenantId,
                                  @PathVariable("cashDrawerId") String cashDrawerId) {
        try {
            return cashDrawerService.get(cashDrawerId, tenantId);
        } catch (Exception e) {
            logger.error(String.format("Cash Drawer not found with ID: %s", cashDrawerId), e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_UPDATE + "', '" + Permission.CASH_DRAWER_CHECKOUT_CLERK_UPDATE + "')")
    @PutMapping(value = "{tenantId}/cashdrawers/{cashDrawerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public CashDrawerResponse update(@PathVariable("cashDrawerId") String cashDrawerId,
                                     @RequestBody @Valid UpdateCashDrawerRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return cashDrawerService.update(cashDrawerId, request, tu.getTenantId());
        } catch (Exception e) {
            logger.error("Error updating cash drawer", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_DELETE + "', '" + Permission.CASH_DRAWER_CHECKOUT_CLERK_DELETE + "')")
    @DeleteMapping(value = "{tenantId}/cashdrawers/{cashDrawerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public void delete(@PathVariable String cashDrawerId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            cashDrawerService.delete(cashDrawerId, tu.getTenantId());
        } catch (Exception e) {
            logger.error("Error deleting cash drawer", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_READ + "', '" + Permission.CASH_DRAWER_CHECKOUT_CLERK_READ + "')")
    @GetMapping(value = "{tenantId}/cashdrawers", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ListCashDrawersResponse getAll(@Valid ListCashDrawersRequestParams params) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return cashDrawerService.getAll(params, tu.getTenantId());
        } catch (Exception e) {
            logger.error("Error listing cash drawers", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_CHECKOUT_READ + "', '" + Permission.CASH_DRAWER_CHECKOUT_CLERK_READ + "')")
    @GetMapping(value = "{tenantId}/cashdrawers/{cashDrawerId}/checkout", produces = { MediaType.APPLICATION_JSON_VALUE })
    public CheckoutDetailsResponse getCheckoutDetails(@PathVariable String cashDrawerId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return cashDrawerService.getCheckoutDetails(cashDrawerId, tu.getTenantId());
        } catch (Exception e) {
            logger.error("Error getting checkout details for cash drawer", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_READ + "', '" + Permission.SERVER_CASH_DRAWER_READ + "', '" + Permission.CASH_DRAWER_CHECKOUT_CLERK_READ + "')")
    @GetMapping(value = "{tenantId}/cashdrawers/users/{username}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ListCashDrawersResponse getByAssignedUsers(@PathVariable String tenantId,
                                                      @PathVariable String username) {
        try {
            return cashDrawerService.getByAssignedUser(username, tenantId);
        } catch (Exception e) {
            logger.error("Error getting cash drawers by username");
            throw e;
        }
    }

    /**
     * Heartbeat method to check if cash-drawer service is up and running
     *
     */
    @RequestMapping("{tenantId}/cashdrawers/health")
    public String health() {
        return "\"CashDrawer service is up!\"";
    }
}

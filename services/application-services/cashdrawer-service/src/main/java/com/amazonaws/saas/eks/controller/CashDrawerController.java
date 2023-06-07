package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.dto.requests.cashdrawers.CreateCashDrawerRequest;
import com.amazonaws.saas.eks.dto.requests.cashdrawers.ListCashDrawersRequestParams;
import com.amazonaws.saas.eks.dto.requests.cashdrawers.UpdateCashDrawerRequest;
import com.amazonaws.saas.eks.dto.responses.cashdrawers.CashDrawerResponse;
import com.amazonaws.saas.eks.dto.responses.cashdrawers.checkout.CheckoutDetailsResponse;
import com.amazonaws.saas.eks.dto.responses.cashdrawers.checkout.ListCashDrawerAdminResponse;
import com.amazonaws.saas.eks.dto.responses.cashdrawers.ListCashDrawersResponse;
import com.amazonaws.saas.eks.model.Permission;
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

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_CREATE + "')")
    @PostMapping(value = "{tenantId}/orders/cash-drawers", produces = { MediaType.APPLICATION_JSON_VALUE })
    public CashDrawerResponse create(@RequestBody @Valid CreateCashDrawerRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return cashDrawerService.create(request, tu.getTenantId());
        } catch (Exception e) {
            logger.error("Error creating cash drawer", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_READ + "')")
    @GetMapping(value = "{tenantId}/orders/cash-drawers/{cashDrawerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public CashDrawerResponse get(@PathVariable("cashDrawerId") String cashDrawerId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return cashDrawerService.get(cashDrawerId, tu.getTenantId());
        } catch (Exception e) {
            logger.error(String.format("Cash Drawer not found with ID: %s", cashDrawerId), e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_UPDATE + "')")
    @PutMapping(value = "{tenantId}/orders/cash-drawers/{cashDrawerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
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

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_DELETE + "')")
    @DeleteMapping(value = "{tenantId}/orders/cash-drawers/{cashDrawerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public void delete(@PathVariable String cashDrawerId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            cashDrawerService.delete(cashDrawerId, tu.getTenantId());
        } catch (Exception e) {
            logger.error("Error deleting cash drawer", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_READ + "')")
    @GetMapping(value = "{tenantId}/orders/cash-drawers", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ListCashDrawersResponse getAll(@Valid ListCashDrawersRequestParams params) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return cashDrawerService.getAll(params, tu.getTenantId());
        } catch (Exception e) {
            logger.error("Error listing cash drawers", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_CHECKOUT_READ + "')")
    @GetMapping(value = "{tenantId}/orders/cash-drawers/checkout", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ListCashDrawerAdminResponse getAllAdmin(@Valid ListCashDrawersRequestParams params) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return cashDrawerService.getAllAdmin(params, tu.getTenantId());
        } catch (Exception e) {
            logger.error("Error listing cash drawers admin view", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_CHECKOUT_READ + "')")
    @GetMapping(value = "{tenantId}/orders/cash-drawers/{cashDrawerId}/checkout", produces = { MediaType.APPLICATION_JSON_VALUE })
    public CheckoutDetailsResponse getCheckoutDetails(@PathVariable String cashDrawerId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return cashDrawerService.getCheckoutDetails(cashDrawerId, tu.getTenantId());
        } catch (Exception e) {
            logger.error("Error getting checkout details for cash drawer", e);
            throw e;
        }
    }
}

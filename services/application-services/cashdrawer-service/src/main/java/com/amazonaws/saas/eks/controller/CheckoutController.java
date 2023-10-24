package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.CheckoutDetailsResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.ListCheckoutResponse;
import com.amazonaws.saas.eks.cashdrawer.model.Permission;
import com.amazonaws.saas.eks.service.CheckoutService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class CheckoutController {
    private static final Logger logger = LogManager.getLogger(CheckoutController.class);

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private JwtAuthManager jwtAuthManager;

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_READ + "', '" + Permission.SERVER_CASH_DRAWER_READ + "', '" + Permission.CASH_DRAWER_CHECKOUT_CLERK_READ + "')")
    @GetMapping(value = "{tenantId}/cashdrawers/checkouts", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ListCheckoutResponse get(HttpServletRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();

            // If user contains admin permission, retrieve all checkouts
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            var auth = new SimpleGrantedAuthority(Permission.CASH_DRAWER_READ);
            String username = authentication.getAuthorities().contains(auth) ? "" : tu.getUsername();
            return checkoutService.get(tu.getTenantId(), username);
        } catch (Exception e) {
            logger.error("Error listing checkout history", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CASH_DRAWER_READ + "', '" + Permission.SERVER_CASH_DRAWER_READ + "', '" + Permission.CASH_DRAWER_CHECKOUT_CLERK_READ + "')")
    @GetMapping(value = "{tenantId}/cashdrawers/checkouts/{checkoutId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public CheckoutDetailsResponse getById(@PathVariable String checkoutId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return checkoutService.getById(tu.getTenantId(), checkoutId);
        } catch (Exception e) {
            logger.error("Error getting checkout details", e);
            throw e;
        }
    }
}

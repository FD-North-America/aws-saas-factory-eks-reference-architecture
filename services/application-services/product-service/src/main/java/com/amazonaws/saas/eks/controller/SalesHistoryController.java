package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.product.dto.responses.saleshistory.ListSalesHistoryResponse;
import com.amazonaws.saas.eks.product.model.Permission;
import com.amazonaws.saas.eks.service.SalesHistoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class SalesHistoryController {
    private static final Logger logger = LogManager.getLogger(SalesHistoryController.class);

    @Autowired
    private JwtAuthManager jwtAuthManager;

    @Autowired
    private SalesHistoryService salesHistoryService;

    @PreAuthorize("hasAnyAuthority('" + Permission.PRODUCT_READ + "')")
    @GetMapping(value = "{tenantId}/products/{productId}/sales-history", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ListSalesHistoryResponse getByProduct(@PathVariable("productId") String productId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return salesHistoryService.getByProduct(tu.getTenantId(), productId);
        } catch (Exception e) {
            logger.error("Error listing sales histories", e);
            throw e;
        }
    }
}

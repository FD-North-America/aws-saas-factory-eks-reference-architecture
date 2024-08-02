package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.order.dto.requests.tax.CreateTaxRequest;
import com.amazonaws.saas.eks.order.dto.requests.tax.UpdateTaxRequest;
import com.amazonaws.saas.eks.order.dto.responses.tax.TaxResponse;
import com.amazonaws.saas.eks.service.TaxService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class TaxController {
    private static final Logger logger = LogManager.getLogger(TaxController.class);

    @Autowired
    private TaxService taxService;

    @Autowired
    private JwtAuthManager jwtAuthManager;

    @PostMapping(value = "{tenantId}/orders/taxes", produces = {MediaType.APPLICATION_JSON_VALUE})
    public TaxResponse add(@RequestBody @Valid CreateTaxRequest request) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            return taxService.create(request, tenantId);
        } catch (Exception e) {
            logger.error("Error adding tax info to an order. Tenant ID: {}", tenantId);
            throw e;
        }
    }

    @GetMapping(value = "{tenantId}/orders/taxes/{taxId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public TaxResponse get(@PathVariable String taxId) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            return taxService.get(taxId, tenantId);
        } catch (Exception e) {
            logger.error("Error fetching tax info of an order. Tax ID: {}. Tenant ID: {}", taxId, tenantId);
            throw e;
        }
    }

    @GetMapping(value = "{tenantId}/orders/{orderId}/taxes", produces = {MediaType.APPLICATION_JSON_VALUE})
    public TaxResponse getByOrderId(@PathVariable String orderId) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            return taxService.getByOrderId(orderId, tenantId);
        } catch (Exception e) {
            logger.error("Error fetching tax info of an order. Order ID: {}. Tenant ID: {}", orderId, tenantId);
            throw e;
        }
    }

    @PutMapping(value = "{tenantId}/orders/taxes/{taxId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public TaxResponse update(@PathVariable String taxId, @RequestBody @Valid UpdateTaxRequest request) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            return taxService.update(taxId, tenantId, request);
        } catch (Exception e) {
            logger.error("Error updating tax info of an order. Tax ID: {}. Tenant ID: {}", taxId, tenantId);
            throw e;
        }
    }

    @DeleteMapping(value = "{tenantId}/orders/taxes/{taxId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public void delete(@PathVariable String taxId) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            taxService.delete(taxId, tenantId);
        } catch (Exception e) {
            logger.error("Error deleting tax info of an order. Tax ID: {}. Tenant ID: {}", taxId, tenantId);
            throw e;
        }
    }
}

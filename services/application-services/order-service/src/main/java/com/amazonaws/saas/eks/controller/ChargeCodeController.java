package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.order.dto.requests.CreateChargeCodeRequest;
import com.amazonaws.saas.eks.order.dto.requests.UpdateChargeCodeRequest;
import com.amazonaws.saas.eks.order.dto.responses.ChargeCodeResponse;
import com.amazonaws.saas.eks.service.ChargeCodeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ChargeCodeController {
    private static final Logger logger = LogManager.getLogger(ChargeCodeController.class);

    @Autowired
    private ChargeCodeService chargeCodeService;

    @Autowired
    private JwtAuthManager jwtAuthManager;

    @PostMapping(value = "{tenantId}/orders/charge-codes", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ChargeCodeResponse add(@RequestBody @Valid CreateChargeCodeRequest request) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            return chargeCodeService.create(tenantId, request);
        } catch (Exception e) {
            logger.error("Error adding charge code. TenantId: {}", tenantId);
            throw e;
        }
    }

    @GetMapping(value = "{tenantId}/orders/charge-codes/{chargeCodeId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ChargeCodeResponse get(@PathVariable String chargeCodeId) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            return chargeCodeService.get(tenantId, chargeCodeId);
        } catch (Exception e) {
            logger.error("Error fetching charge code {}", chargeCodeId);
            throw e;
        }
    }

    @PutMapping(value = "{tenantId}/orders/charge-codes/{chargeCodeId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ChargeCodeResponse update(@PathVariable String chargeCodeId,
                                               @RequestBody @Valid UpdateChargeCodeRequest request) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            return chargeCodeService.update(tenantId, chargeCodeId, request);
        } catch (Exception e) {
            logger.error("Error updating charge code {}. TenantId: {}", chargeCodeId, tenantId);
            throw e;
        }
    }

    @DeleteMapping(value = "{tenantId}/orders/charge-codes/{chargeCodeId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public void delete(@PathVariable String chargeCodeId) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            chargeCodeService.delete(tenantId, chargeCodeId);
        } catch (Exception e) {
            logger.error(String.format("Error deleting charge code. TenantId: %s", tenantId), e);
            throw e;
        }
    }
}

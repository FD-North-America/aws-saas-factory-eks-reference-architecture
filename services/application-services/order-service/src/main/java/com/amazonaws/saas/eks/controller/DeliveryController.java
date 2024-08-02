package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.order.dto.requests.delivery.CreateDeliveryRequest;
import com.amazonaws.saas.eks.order.dto.requests.delivery.UpdateDeliveryRequest;
import com.amazonaws.saas.eks.order.dto.responses.delivery.DeliveryResponse;
import com.amazonaws.saas.eks.service.DeliveryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class DeliveryController {
    private static final Logger logger = LogManager.getLogger(DeliveryController.class);

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private JwtAuthManager jwtAuthManager;

    @PostMapping(value = "{tenantId}/orders/deliveries", produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeliveryResponse addDelivery(@RequestBody @Valid CreateDeliveryRequest request) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            return deliveryService.create(tenantId, request);
        } catch (Exception e) {
            logger.error("error adding delivery. TenantId: {}", tenantId);
            throw e;
        }
    }

    @GetMapping(value = "{tenantId}/orders/deliveries/{deliveryId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeliveryResponse getDelivery(@PathVariable String deliveryId) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            return deliveryService.get(tenantId, deliveryId);
        } catch (Exception e) {
            logger.error("error fetching delivery {}", deliveryId);
            throw e;
        }
    }

    @PutMapping(value = "{tenantId}/orders/deliveries/{deliveryId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeliveryResponse updateDelivery(@PathVariable String deliveryId,
                                           @RequestBody @Valid UpdateDeliveryRequest request) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            return deliveryService.update(tenantId, deliveryId, request);
        } catch (Exception e) {
            logger.error("error updating delivery {}. TenantId: {}", deliveryId, tenantId);
            throw e;
        }
    }

    @DeleteMapping(value = "{tenantId}/orders/deliveries/{deliveryId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public void deleteDelivery(@PathVariable String deliveryId) {
        String tenantId = jwtAuthManager.getTenantUser().getTenantId();
        try {
            deliveryService.delete(tenantId, deliveryId);
        } catch (Exception e) {
            logger.error(String.format("Error deleting delivery. TenantId: %s", tenantId), e);
            throw e;
        }
    }
}

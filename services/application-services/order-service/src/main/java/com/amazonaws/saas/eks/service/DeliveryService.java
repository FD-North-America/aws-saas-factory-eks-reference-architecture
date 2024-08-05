package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.order.dto.requests.delivery.CreateDeliveryRequest;
import com.amazonaws.saas.eks.order.dto.requests.delivery.UpdateDeliveryRequest;
import com.amazonaws.saas.eks.order.dto.responses.delivery.DeliveryResponse;

public interface DeliveryService {
    /**
     * Adds a delivery to the specified Order
     * @param tenantId String
     * @param request {@link CreateDeliveryRequest}
     * @return {@link DeliveryResponse}
     */
    DeliveryResponse create(String tenantId, CreateDeliveryRequest request);

    /**
     * Returns the delivery associated with the order
     * @param tenantId String
     * @param deliveryId String
     * @return {@link DeliveryResponse}
     */
    DeliveryResponse get(String tenantId, String deliveryId);

    /**
     * Updates the delivery associated with the order
     * @param tenantId String
     * @param deliveryId String
     * @param request {@link UpdateDeliveryRequest}
     * @return {@link DeliveryResponse}
     */
    DeliveryResponse update(String tenantId, String deliveryId, UpdateDeliveryRequest request);

    /**
     * Deletes a delivery associated with the order
     * @param tenantId String
     * @param deliveryId String
     */
    void delete(String tenantId, String deliveryId);
}

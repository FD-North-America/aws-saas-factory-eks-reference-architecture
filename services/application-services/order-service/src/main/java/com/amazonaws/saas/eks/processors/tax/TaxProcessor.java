package com.amazonaws.saas.eks.processors.tax;

import com.amazonaws.saas.eks.order.model.Delivery;

import java.math.BigDecimal;


public interface TaxProcessor {
    /**
     * Process the tax calculation for an order
     *
     * @param tenantId Tenant
     * @param taxId    Tax ID associated with the Order
     * @return tax rate to apply to the Order
     */
    BigDecimal process(String tenantId, String taxId);

    /**
     * Handle the address change for an order as it may require a tax update
     *
     * @param tenantId Tenant
     * @param orderId  Order ID
     * @param delivery {@link Delivery}
     */
    void handleAddressChange(String tenantId, String orderId, Delivery delivery);
}

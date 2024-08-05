package com.amazonaws.saas.eks.processors.tax.impl;

import com.amazonaws.saas.eks.order.dto.responses.tax.TaxResponse;
import com.amazonaws.saas.eks.order.mapper.TaxMapper;
import com.amazonaws.saas.eks.order.model.Delivery;
import com.amazonaws.saas.eks.processors.tax.TaxProcessor;
import com.amazonaws.saas.eks.service.TaxService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Service
public class TaxProcessorImpl implements TaxProcessor {

    private final TaxService taxService;

    public TaxProcessorImpl(TaxService taxService) {
        this.taxService = taxService;
    }

    /**
     * Process the tax calculation for an order
     *
     * @param tenantId Tenant
     * @param taxId    Tax ID associated with the Order
     * @return tax rate to apply to the Order
     */
    @Override
    public BigDecimal process(String tenantId, String taxId) {
        TaxResponse tax = taxService.get(taxId, tenantId);
        return StringUtils.hasLength(tax.getCertificateId()) ? BigDecimal.ZERO : tax.getCity().getTax();
    }

    /**
     * Handle the address change for an order as it may require a tax update
     *
     * @param tenantId Tenant
     * @param orderId  Order ID
     * @param delivery {@link Delivery}
     */
    @Override
    public void handleAddressChange(String tenantId, String orderId, Delivery delivery) {
        TaxResponse tax = taxService.getByOrderId(orderId, tenantId);
        taxService.update(tax.getId(), tenantId, TaxMapper.INSTANCE.taxResponseToUpdateTaxRequest(tax));
    }
}

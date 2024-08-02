package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.clients.customer.CustomerServiceClient;
import com.amazonaws.saas.eks.clients.settings.SettingsServiceClient;
import com.amazonaws.saas.eks.customer.dto.responses.certificate.CertificateResponse;
import com.amazonaws.saas.eks.exception.InvalidOrderArgumentsException;
import com.amazonaws.saas.eks.exception.OrderNotFoundException;
import com.amazonaws.saas.eks.order.dto.requests.tax.CreateTaxRequest;
import com.amazonaws.saas.eks.order.dto.requests.tax.UpdateTaxRequest;
import com.amazonaws.saas.eks.order.dto.responses.tax.TaxResponse;
import com.amazonaws.saas.eks.order.mapper.TaxMapper;
import com.amazonaws.saas.eks.order.model.DeliveryAddressLine;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.order.model.Tax;
import com.amazonaws.saas.eks.repository.OrderRepository;
import com.amazonaws.saas.eks.repository.TaxRepository;
import com.amazonaws.saas.eks.service.TaxService;
import com.amazonaws.saas.eks.settings.dto.responses.ListSalesTaxSettingsResponse;
import com.amazonaws.saas.eks.settings.dto.responses.SalesTaxSettingsResponse;
import com.amazonaws.saas.eks.settings.model.enums.SalesTaxJurisdiction;
import feign.FeignException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class TaxServiceImpl implements TaxService {
    private static final Logger logger = LogManager.getLogger(TaxServiceImpl.class);

    @Autowired
    private TaxRepository repository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SettingsServiceClient settingsServiceClient;

    @Autowired
    private CustomerServiceClient customerServiceClient;

    @Override
    public TaxResponse create(CreateTaxRequest request, String tenantId) {
        Order order = orderRepository.getOrderById(request.getOrderId(), tenantId);
        if (order == null) {
            throw new OrderNotFoundException(request.getOrderId());
        }

        Tax tax = repository.getByOrderId(order.getId(), tenantId);
        if (tax != null) {
            throw new InvalidOrderArgumentsException(String.format("Already exists a tax info associated with the provided order. Tax ID: %s",
                    tax.getId()));
        }

        if (StringUtils.hasLength(request.getCertificateId())) {
            validateCertificate(request.getCertificateId(), tenantId);
        }

        tax = TaxMapper.INSTANCE.createTaxRequestToTax(request);
        retrieveTaxInfo(tax, tenantId);
        Tax updatedTax = repository.save(tax, tenantId);

        order.setTaxId(updatedTax.getId());
        orderRepository.save(order, tenantId);

        return TaxMapper.INSTANCE.taxToTaxResponse(updatedTax);
    }

    @Override
    public TaxResponse get(String taxId, String tenantId) {
        Tax tax = repository.get(taxId, tenantId);
        return TaxMapper.INSTANCE.taxToTaxResponse(tax);
    }

    @Override
    public TaxResponse getByOrderId(String orderId, String tenantId) {
        Order order = orderRepository.getOrderById(orderId, tenantId);
        if (order == null) {
            throw new OrderNotFoundException(orderId);
        }

        Tax tax = repository.getByOrderId(orderId, tenantId);
        if (tax == null) {
            throw new InvalidOrderArgumentsException(String.format("There is not tax info associated with the " +
                            "provided order. Order ID: %s", orderId));
        }
        return TaxMapper.INSTANCE.taxToTaxResponse(tax);
    }

    @Override
    public TaxResponse update(String taxId, String tenantId, UpdateTaxRequest request) {
        Tax tax = TaxMapper.INSTANCE.updateTaxRequestToTax(request);
        if (tax.getState() != null && StringUtils.hasLength(tax.getState().getAddress())) {
            retrieveTaxInfoBy(tax.getState(), SalesTaxJurisdiction.STATE, tenantId);
        }
        if (tax.getCounty() != null && StringUtils.hasLength(tax.getCounty().getAddress())) {
            retrieveTaxInfoBy(tax.getCounty(), SalesTaxJurisdiction.COUNTY, tenantId);
        }
        if (tax.getCity() != null && StringUtils.hasLength(tax.getCity().getAddress())) {
            retrieveTaxInfoBy(tax.getCity(), SalesTaxJurisdiction.CITY, tenantId);
        }
        if (StringUtils.hasLength(tax.getCertificateId())) {
            validateCertificate(request.getCertificateId(), tenantId);
        }
        Tax updatedTax = repository.update(taxId, tenantId, tax);
        return TaxMapper.INSTANCE.taxToTaxResponse(updatedTax);
    }

    @Override
    public void delete(String taxId, String tenantId) {
        TaxResponse tax = get(taxId, tenantId);

        Order order = orderRepository.getOrderById(tax.getOrderId(), tenantId);
        if (order == null) {
            throw new OrderNotFoundException(tax.getOrderId());
        }
        order.setTaxId(null);
        orderRepository.save(order, tenantId);

        repository.delete(taxId, tenantId);
    }

    private void retrieveTaxInfo(Tax tax, String tenantId) {
        // Retrieve tax code and rate by state/county/city from settings service
        ListSalesTaxSettingsResponse response = settingsServiceClient.getAllSalesTax(tenantId, null, null).getBody();
        if (response == null || response.getSalesTaxes() == null || response.getSalesTaxes().isEmpty()) {
            throw new InvalidOrderArgumentsException("The tax info cannot be retrieved.");
        }
        for (SalesTaxSettingsResponse stateTax: response.getSalesTaxes()) {
            if (tax.getState().getAddress().equalsIgnoreCase(stateTax.getCode())) {
                setTaxInfo(tax.getState(), stateTax);

                for (SalesTaxSettingsResponse countyTax: stateTax.getSalesTaxes()) {
                    if (tax.getCounty().getAddress().equalsIgnoreCase(countyTax.getCode())) {
                        setTaxInfo(tax.getCounty(), countyTax);

                        for (SalesTaxSettingsResponse cityTax: countyTax.getSalesTaxes()) {
                            if (tax.getCity().getAddress().equalsIgnoreCase(cityTax.getCode())) {
                                setTaxInfo(tax.getCity(), cityTax);
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
        if (tax.getState().getTaxCode() == null || tax.getCounty().getTaxCode() == null
                || tax.getCity().getTaxCode() == null) {
            throw new InvalidOrderArgumentsException("The state/county/city provided is not valid.");
        }
    }

    private void setTaxInfo(DeliveryAddressLine deliveryAddressLine, SalesTaxSettingsResponse salesTaxSettingsResponse) {
        deliveryAddressLine.setTaxCode(salesTaxSettingsResponse.getCode());
        Float rate = salesTaxSettingsResponse.getRate();
        BigDecimal taxRate = new BigDecimal(Float.toString(rate));
        deliveryAddressLine.setTax(taxRate);
    }

    private void retrieveTaxInfoBy(DeliveryAddressLine deliveryAddressLine, SalesTaxJurisdiction jurisdiction, String tenantId) {
        ListSalesTaxSettingsResponse response = settingsServiceClient.getAllSalesTax(
                tenantId,
                deliveryAddressLine.getAddress(),
                jurisdiction.toString()
        ).getBody();
        if (response == null || response.getSalesTaxes() == null || response.getSalesTaxes().isEmpty()) {
            throw new InvalidOrderArgumentsException("The state/county/city provided is not valid.");
        }
        for (SalesTaxSettingsResponse salesTaxSettings: response.getSalesTaxes()) {
            if (deliveryAddressLine.getAddress().equalsIgnoreCase(salesTaxSettings.getCode())) {
                setTaxInfo(deliveryAddressLine, salesTaxSettings);
                break;
            }
        }
    }

    private void validateCertificate(String certId, String tenantId) {
        CertificateResponse cert = null;
        try {
            cert = customerServiceClient.get(certId, tenantId).getBody();
        } catch (FeignException e) {
            logger.error("Error while fetching customer's certificate", e);
        }
        if (cert == null) {
            throw new InvalidOrderArgumentsException(String.format("Certificate not found. ID: %s", certId));
        }
        if (cert.getExpiryDate().before(new Date())) {
            throw new InvalidOrderArgumentsException(String.format("Certificate already expired. ID: %s", certId));
        }
    }
}

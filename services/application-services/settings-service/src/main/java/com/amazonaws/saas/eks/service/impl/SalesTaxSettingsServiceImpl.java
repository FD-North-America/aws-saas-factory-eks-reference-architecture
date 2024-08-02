package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.repository.SalesTaxSettingsRepository;
import com.amazonaws.saas.eks.service.SalesTaxSettingsService;
import com.amazonaws.saas.eks.settings.dto.requests.salestax.CreateSalesTaxSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.salestax.ListSalesTaxSettingsRequestParams;
import com.amazonaws.saas.eks.settings.dto.requests.salestax.UpdateSalesTaxSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.ListSalesTaxSettingsResponse;
import com.amazonaws.saas.eks.settings.dto.responses.SalesTaxSettingsResponse;
import com.amazonaws.saas.eks.settings.mapper.SalesTaxSettingsMapper;
import com.amazonaws.saas.eks.settings.model.v2.salestax.SalesTaxSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SalesTaxSettingsServiceImpl implements SalesTaxSettingsService {
    private static final Logger logger = LogManager.getLogger(SalesTaxSettingsServiceImpl.class);

    @Autowired
    private SalesTaxSettingsRepository salesTaxSettingsRepository;

    @Override
    public SalesTaxSettingsResponse create(String tenantId, CreateSalesTaxSettingsRequest request) {
        SalesTaxSettings salesTaxSettings = SalesTaxSettingsMapper.INSTANCE.createSalesTaxSettingsRequestToSalesTaxSettings(request);
        salesTaxSettings.setCreated(new Date());
        salesTaxSettings.setModified(salesTaxSettings.getCreated());

        SalesTaxSettings createdSalesTaxSettings = salesTaxSettingsRepository.insert(tenantId, request.getParentId(), salesTaxSettings);

        return SalesTaxSettingsMapper.INSTANCE.salesTaxSettingsToSalesTaxSettingsResponse(createdSalesTaxSettings);
    }

    @Override
    public SalesTaxSettingsResponse get(String tenantId, String id) {
        SalesTaxSettings salesTaxSettings = salesTaxSettingsRepository.get(tenantId, id);
        return SalesTaxSettingsMapper.INSTANCE.salesTaxSettingsToSalesTaxSettingsResponse(salesTaxSettings);
    }

    @Override
    public ListSalesTaxSettingsResponse getAll(String tenantId, ListSalesTaxSettingsRequestParams params) {
        ListSalesTaxSettingsResponse response = new ListSalesTaxSettingsResponse();

        List<SalesTaxSettings> salesTaxSettingsList = salesTaxSettingsRepository.getAll(tenantId, params.getFilter(), params.getJurisdiction(), params.getState(), params.getCity());

        for (SalesTaxSettings x: salesTaxSettingsList) {
            SalesTaxSettingsResponse xr = SalesTaxSettingsMapper.INSTANCE.salesTaxSettingsToSalesTaxSettingsResponse(x);
            response.getSalesTaxes().add(xr);
        }
        return response;
    }

    @Override
    public SalesTaxSettingsResponse update(String tenantId, String id, UpdateSalesTaxSettingsRequest request) {
        SalesTaxSettings salesTaxSettings = SalesTaxSettingsMapper.INSTANCE.updateSalesTaxSettingsRequestToSalesTaxSettings(request);
        SalesTaxSettings updatedSalesTaxSettings = salesTaxSettingsRepository.update(tenantId, id, request.getNewParentId(), salesTaxSettings);
        return SalesTaxSettingsMapper.INSTANCE.salesTaxSettingsToSalesTaxSettingsResponse(updatedSalesTaxSettings);
    }

    @Override
    public void delete(String tenantId, String salesTaxSettingsId) {
        SalesTaxSettings model = salesTaxSettingsRepository.get(tenantId, salesTaxSettingsId);
        salesTaxSettingsRepository.delete(model);
    }
}

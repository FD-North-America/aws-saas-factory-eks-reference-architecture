package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.dto.requests.uom.CreateUOMRequest;
import com.amazonaws.saas.eks.dto.requests.uom.ListUOMRequestParams;
import com.amazonaws.saas.eks.dto.requests.uom.UpdateUOMRequest;
import com.amazonaws.saas.eks.dto.responses.uom.ListUOMResponse;
import com.amazonaws.saas.eks.dto.responses.uom.UOMResponse;
import com.amazonaws.saas.eks.exception.InvalidUOMNameException;
import com.amazonaws.saas.eks.exception.InvalidUOMProductIdException;
import com.amazonaws.saas.eks.mapper.UOMMapper;
import com.amazonaws.saas.eks.model.Product;
import com.amazonaws.saas.eks.model.Settings;
import com.amazonaws.saas.eks.model.UOM;
import com.amazonaws.saas.eks.repository.ProductRepository;
import com.amazonaws.saas.eks.repository.SettingsRepository;
import com.amazonaws.saas.eks.repository.UOMRepository;
import com.amazonaws.saas.eks.repository.VolumePricingRepository;
import com.amazonaws.saas.eks.service.UOMService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UOMServiceImpl implements UOMService {
    private static final Logger logger = LogManager.getLogger(UOMServiceImpl.class);

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private UOMRepository uomRepository;

    @Autowired
    private VolumePricingRepository volumePricingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public UOMResponse create(String tenantId, CreateUOMRequest request) {
        if (request.getProductId() == null) {
            throw new InvalidUOMProductIdException(tenantId, null);
        }
        UOM uom = UOMMapper.INSTANCE.createUOMRequestToUOM(request);
        checkUomName(tenantId, uom.getName());
        UOM model = uomRepository.insert(tenantId, uom);
        return UOMMapper.INSTANCE.uomToUomResponse(model);
    }

    @Override
    public UOMResponse get(String tenantId, String id) {
        UOM model = uomRepository.get(tenantId, id);
        return UOMMapper.INSTANCE.uomToUomResponse(model);
    }

    @Override
    public ListUOMResponse getAll(String tenantId, ListUOMRequestParams params) {
        ListUOMResponse response = new ListUOMResponse();
        List<UOM> uomList = uomRepository.getUOMbyProductId(tenantId, params.getProductId());
        List<UOMResponse> uomResponseList = uomList.stream().map(UOMMapper.INSTANCE::uomToUomResponse)
                .collect(Collectors.toList());
        response.getUnitsOfMeasure().addAll(uomResponseList);
        return response;
    }

    @Override
    public UOMResponse update(String tenantId, String uomId, UpdateUOMRequest request) {
        UOM uom = UOMMapper.INSTANCE.updateUOMRequestToUOM(request);
        checkUomName(tenantId, uom.getName());
        UOM updatedUom = uomRepository.update(tenantId, uomId, uom);

        // If UOM factor changes, update VolumePricing factor and price
        if (request.getFactor() != null && request.getFactor() > 0) {
            Product product = productRepository.get(tenantId, updatedUom.getProductId());
            volumePricingRepository.updateOnUOMFactorChange(tenantId, product, updatedUom);
        }

        return UOMMapper.INSTANCE.uomToUomResponse(updatedUom);
    }

    @Override
    public void delete(String tenantId, String id) {
        uomRepository.delete(tenantId, id);
    }

    private void checkUomName(String tenantId, String name) {
        if (name != null) {
            Settings settings = settingsRepository.get(tenantId);
            if (!settings.getUnitOfMeasureNames().contains(name)) {
                throw new InvalidUOMNameException(tenantId, name);
            }
        }
    }
}

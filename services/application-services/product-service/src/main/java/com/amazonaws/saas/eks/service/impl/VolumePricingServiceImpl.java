package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.dto.requests.volumepricing.CreateVolumePricingRequest;
import com.amazonaws.saas.eks.dto.requests.volumepricing.ListVolumePricingRequestParams;
import com.amazonaws.saas.eks.dto.requests.volumepricing.UpdateVolumePricingRequest;
import com.amazonaws.saas.eks.dto.responses.volumepricing.ListVolumePricingResponse;
import com.amazonaws.saas.eks.dto.responses.volumepricing.VolumePricingResponse;
import com.amazonaws.saas.eks.mapper.VolumePricingMapper;
import com.amazonaws.saas.eks.model.*;
import com.amazonaws.saas.eks.repository.ProductRepository;
import com.amazonaws.saas.eks.repository.UOMRepository;
import com.amazonaws.saas.eks.repository.VolumePricingRepository;
import com.amazonaws.saas.eks.service.VolumePricingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class VolumePricingServiceImpl implements VolumePricingService {
    private static final Logger logger = LogManager.getLogger(VolumePricingServiceImpl.class);

    @Autowired
    private VolumePricingRepository volumePricingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UOMRepository uomRepository;

    @Override
    public VolumePricingResponse create(String tenantId, CreateVolumePricingRequest request) {
        UOM uom = uomRepository.get(tenantId, request.getUomId());

        Product product = productRepository.get(tenantId, uom.getProductId());

        VolumePricing volumePricing = VolumePricingMapper.INSTANCE.createVolumePricingRequestToVolumePricing(request);
        volumePricing.setId(buildId());
        volumePricing.setProductId(product.getId());

        Double factor = volumePricingRepository.computeFactor(uom.getFactor(), volumePricing.getBreakPointQty());
        volumePricing.setFactor(factor);

        BigDecimal price = volumePricingRepository.computePrice(volumePricing.getMode(), factor,
                product.getRetailPrice(), volumePricing.getDiscount());
        volumePricing.setPrice(price);

        volumePricing.setCreated(new Date());
        volumePricing.setModified(volumePricing.getCreated());

        volumePricingRepository.insert(tenantId, volumePricing);

        return get(tenantId, volumePricing.getId());
    }

    @Override
    public VolumePricingResponse get(String tenantId, String id) {
        VolumePricing volumePricing = volumePricingRepository.get(tenantId, id);
        return VolumePricingMapper.INSTANCE.volumePricingToVolumePricingResponse(volumePricing);
    }

    @Override
    public ListVolumePricingResponse getAll(String tenantId, ListVolumePricingRequestParams params) {
        ListVolumePricingResponse response = new ListVolumePricingResponse();

        List<VolumePricing> volumePricingList = volumePricingRepository.getAll(tenantId, params.getProductId());
        for (VolumePricing vp : volumePricingList) {
            VolumePricingResponse vpr = VolumePricingMapper.INSTANCE.volumePricingToVolumePricingResponse(vp);
            response.getVolumePricingList().add(vpr);
        }

        return response;
    }

    @Override
    public VolumePricingResponse update(String tenantId, String id, UpdateVolumePricingRequest request) {
        VolumePricing volumePricing = volumePricingRepository.get(tenantId, id);
        UOM uom = uomRepository.get(tenantId, volumePricing.getUomId());
        Product product = productRepository.get(tenantId, uom.getProductId());

        if (!StringUtils.isEmpty(request.getBreakPointName())) {
            volumePricing.setBreakPointName(request.getBreakPointName());
        }

        boolean updatePrice = false;
        if (request.getBreakPointQty() != null) {
            volumePricing.setBreakPointQty(request.getBreakPointQty());
            Double factor = volumePricingRepository.computeFactor(uom.getFactor(), volumePricing.getBreakPointQty());
            volumePricing.setFactor(factor);
            updatePrice = true;
        }

        if (!StringUtils.isEmpty(request.getMode())) {
            volumePricing.setMode(request.getMode());
        }

        if (request.getDiscount() != null && request.getDiscount().compareTo(BigDecimal.ZERO) >= 0) {
            volumePricing.setDiscount(request.getDiscount());
            updatePrice = true;
        }

        if (request.getActive() != null) {
            volumePricing.setActive(request.getActive());
        }

        if (updatePrice) {
            BigDecimal price = volumePricingRepository.computePrice(volumePricing.getMode(), volumePricing.getFactor(),
                    product.getRetailPrice(), volumePricing.getDiscount());
            volumePricing.setPrice(price);
        }

        VolumePricing updatedVolumePricing = volumePricingRepository.update(tenantId, volumePricing);

        return VolumePricingMapper.INSTANCE.volumePricingToVolumePricingResponse(updatedVolumePricing);
    }

    @Override
    public void delete(String tenantId, String id) {
        volumePricingRepository.delete(tenantId, id);
    }

    private String buildId() {
        return String.valueOf(UUID.randomUUID());
    }
}

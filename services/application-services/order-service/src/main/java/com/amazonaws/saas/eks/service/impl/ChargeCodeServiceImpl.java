package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.order.dto.requests.CreateChargeCodeRequest;
import com.amazonaws.saas.eks.order.dto.requests.UpdateChargeCodeRequest;
import com.amazonaws.saas.eks.order.dto.responses.ChargeCodeResponse;
import com.amazonaws.saas.eks.order.mapper.ChargeCodeMapper;
import com.amazonaws.saas.eks.order.model.ChargeCode;
import com.amazonaws.saas.eks.repository.ChargeCodeRepository;
import com.amazonaws.saas.eks.service.ChargeCodeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ChargeCodeServiceImpl implements ChargeCodeService {
    private static final Logger logger = LogManager.getLogger(ChargeCodeServiceImpl.class);

    private final ChargeCodeRepository repository;

    public ChargeCodeServiceImpl(ChargeCodeRepository repository) {
        this.repository = repository;
    }

    /**
     * Adds a charge code to the specified order
     *
     * @param tenantId String
     * @param request  {@link CreateChargeCodeRequest}
     * @return {@link ChargeCodeResponse}
     */
    @Override
    public ChargeCodeResponse create(String tenantId, CreateChargeCodeRequest request) {
        ChargeCode chargeCode = ChargeCodeMapper.INSTANCE.createChargeCodeRequestToChargeCode(request);
        ChargeCode updatedChargeCode = repository.save(tenantId, chargeCode);
        return ChargeCodeMapper.INSTANCE.chargeCodeToChargeCodeResponse(updatedChargeCode);
    }

    /**
     * Returns the charge code associated with the order
     *
     * @param tenantId   String
     * @param chargeCodeId String
     * @return {@link ChargeCodeResponse}
     */
    @Override
    public ChargeCodeResponse get(String tenantId, String chargeCodeId) {
        ChargeCode chargeCode = repository.get(tenantId, chargeCodeId);
        return ChargeCodeMapper.INSTANCE.chargeCodeToChargeCodeResponse(chargeCode);
    }

    /**
     * Updates the charge code associated with the order
     *
     * @param tenantId String
     * @param chargeCodeId String
     * @param request {@link UpdateChargeCodeRequest}
     * @return {@link ChargeCodeResponse}
     */
    @Override
    public ChargeCodeResponse update(String tenantId, String chargeCodeId, UpdateChargeCodeRequest request) {
        ChargeCode model = repository.get(tenantId, chargeCodeId);
        ChargeCode chargeCode = ChargeCodeMapper.INSTANCE.updateChargeCodeRequestToChargeCode(request);
        chargeCode.setId(chargeCodeId);
        chargeCode.setOrderId(model.getOrderId());
        ChargeCode updatedChargeCode = repository.save(tenantId, chargeCode);
        return ChargeCodeMapper.INSTANCE.chargeCodeToChargeCodeResponse(updatedChargeCode);
    }

    /**
     * Deletes a charge code associated with the order
     *
     * @param tenantId String
     * @param chargeCodeId String
     */
    @Override
    public void delete(String tenantId, String chargeCodeId) {
        repository.delete(tenantId, chargeCodeId);
    }
}

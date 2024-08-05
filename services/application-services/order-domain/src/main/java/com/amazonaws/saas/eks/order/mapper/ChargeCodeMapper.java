package com.amazonaws.saas.eks.order.mapper;

import com.amazonaws.saas.eks.order.dto.requests.CreateChargeCodeRequest;
import com.amazonaws.saas.eks.order.dto.requests.UpdateChargeCodeRequest;
import com.amazonaws.saas.eks.order.dto.responses.ChargeCodeListItem;
import com.amazonaws.saas.eks.order.dto.responses.ChargeCodeResponse;
import com.amazonaws.saas.eks.order.model.ChargeCode;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChargeCodeMapper {
    ChargeCodeMapper INSTANCE = Mappers.getMapper(ChargeCodeMapper.class);

    ChargeCode createChargeCodeRequestToChargeCode(CreateChargeCodeRequest request);

    ChargeCodeResponse chargeCodeToChargeCodeResponse(ChargeCode chargeCode);

    ChargeCode updateChargeCodeRequestToChargeCode(UpdateChargeCodeRequest request);

    ChargeCodeListItem chargeCodeToChargeCodeListItem(ChargeCode chargeCode);
}

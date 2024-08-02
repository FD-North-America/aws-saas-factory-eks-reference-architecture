package com.amazonaws.saas.eks.settings.mapper;

import com.amazonaws.saas.eks.settings.dto.requests.reasoncodes.CreateReasonCodesSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.reasoncodes.UpdateReasonCodesSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.ReasonCodesSettingsResponse;
import com.amazonaws.saas.eks.settings.model.v2.reasoncodes.ReasonCodesSettings;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReasonCodesSettingsMapper {
    ReasonCodesSettingsMapper INSTANCE = Mappers.getMapper(ReasonCodesSettingsMapper.class);

    ReasonCodesSettings createReasonCodesSettingsRequestToReasonCodesSettings(CreateReasonCodesSettingsRequest request);

    ReasonCodesSettingsResponse reasonCodesSettingsToReasonCodesSettingsResponse(ReasonCodesSettings settings);

    ReasonCodesSettings updateReasonCodesSettingsRequestToReasonCodesSettings(UpdateReasonCodesSettingsRequest request);
}

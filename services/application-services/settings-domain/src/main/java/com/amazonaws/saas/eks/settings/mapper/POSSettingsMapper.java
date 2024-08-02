package com.amazonaws.saas.eks.settings.mapper;

import com.amazonaws.saas.eks.settings.dto.requests.pos.CreatePOSSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.pos.UpdatePOSSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.POSSettingsResponse;
import com.amazonaws.saas.eks.settings.model.v2.pos.POSSettings;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface POSSettingsMapper {
    POSSettingsMapper INSTANCE = Mappers.getMapper(POSSettingsMapper.class);

    POSSettings createPOSSettingsRequestToPOSSettings(CreatePOSSettingsRequest request);

    POSSettingsResponse posSettingsToPOSSettingsResponse(POSSettings settings);

    POSSettings updatePOSSettingsRequestToPOSSettings(UpdatePOSSettingsRequest request);
}

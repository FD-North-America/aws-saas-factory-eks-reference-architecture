package com.amazonaws.saas.eks.mapper;

import com.amazonaws.saas.eks.dto.responses.settings.SettingsResponse;
import com.amazonaws.saas.eks.settings.model.Settings;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SettingsMapper {
    SettingsMapper INSTANCE = Mappers.getMapper(SettingsMapper.class);

    SettingsResponse settingsToSettingsResponse(Settings settings);
}

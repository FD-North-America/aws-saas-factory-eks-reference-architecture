package com.amazonaws.saas.eks.settings.mapper;

import com.amazonaws.saas.eks.settings.dto.requests.inventory.CreateInventorySettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.inventory.UpdateInventorySettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.InventorySettingsResponse;
import com.amazonaws.saas.eks.settings.model.v2.inventory.InventorySettings;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InventorySettingsMapper {
    InventorySettingsMapper INSTANCE = Mappers.getMapper(InventorySettingsMapper.class);

    InventorySettings createInventorySettingsRequestToInventorySettings(CreateInventorySettingsRequest request);

    InventorySettingsResponse inventorySettingsToInventorySettingsResponse(InventorySettings settings);

    InventorySettings updateInventorySettingsRequestToInventorySettings(UpdateInventorySettingsRequest request);
}

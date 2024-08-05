package com.amazonaws.saas.eks.settings.mapper;

import com.amazonaws.saas.eks.settings.dto.requests.purchasing.CreatePurchasingSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.purchasing.UpdatePurchasingSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.PurchasingSettingsResponse;
import com.amazonaws.saas.eks.settings.model.v2.purchasing.PurchasingSettings;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PurchasingSettingsMapper {
    PurchasingSettingsMapper INSTANCE = Mappers.getMapper(PurchasingSettingsMapper.class);

    PurchasingSettings createPurchasingSettingsRequestToPurchasingSettings(CreatePurchasingSettingsRequest request);

    PurchasingSettingsResponse purchasingSettingsToPurchasingSettingsResponse(PurchasingSettings settings);

    PurchasingSettings updatePurchasingSettingsRequestToPurchasingSettings(UpdatePurchasingSettingsRequest request);
}

package com.amazonaws.saas.eks.settings.mapper;

import com.amazonaws.saas.eks.settings.dto.requests.salestax.CreateSalesTaxSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.salestax.UpdateSalesTaxSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.SalesTaxSettingsResponse;
import com.amazonaws.saas.eks.settings.model.v2.salestax.SalesTaxSettings;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SalesTaxSettingsMapper {
    SalesTaxSettingsMapper INSTANCE = Mappers.getMapper(SalesTaxSettingsMapper.class);

    SalesTaxSettings createSalesTaxSettingsRequestToSalesTaxSettings(CreateSalesTaxSettingsRequest request);

    SalesTaxSettings updateSalesTaxSettingsRequestToSalesTaxSettings(UpdateSalesTaxSettingsRequest request);

    SalesTaxSettingsResponse salesTaxSettingsToSalesTaxSettingsResponse(SalesTaxSettings settings);
}

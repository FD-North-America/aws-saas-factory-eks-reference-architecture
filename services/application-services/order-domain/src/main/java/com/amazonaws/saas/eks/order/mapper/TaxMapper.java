package com.amazonaws.saas.eks.order.mapper;

import com.amazonaws.saas.eks.order.dto.requests.tax.CreateTaxRequest;
import com.amazonaws.saas.eks.order.dto.requests.tax.UpdateTaxRequest;
import com.amazonaws.saas.eks.order.dto.responses.tax.TaxResponse;
import com.amazonaws.saas.eks.order.model.Tax;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaxMapper {
    TaxMapper INSTANCE = Mappers.getMapper(TaxMapper.class);

    @Mapping(source = "city", target = "city.address")
    @Mapping(source = "county", target = "county.address")
    @Mapping(source = "state", target = "state.address")
    Tax createTaxRequestToTax(CreateTaxRequest request);

    TaxResponse taxToTaxResponse(Tax tax);

    @Mapping(source = "city", target = "city.address")
    @Mapping(source = "county", target = "county.address")
    @Mapping(source = "state", target = "state.address")
    Tax updateTaxRequestToTax(UpdateTaxRequest request);
}

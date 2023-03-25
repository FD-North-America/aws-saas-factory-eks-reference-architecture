package com.amazonaws.saas.eks.mapper;

import com.amazonaws.saas.eks.dto.requests.uom.CreateUOMRequest;
import com.amazonaws.saas.eks.dto.requests.uom.UpdateUOMRequest;
import com.amazonaws.saas.eks.dto.responses.uom.UOMResponse;
import com.amazonaws.saas.eks.model.UOM;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UOMMapper {
    UOMMapper INSTANCE = Mappers.getMapper(UOMMapper.class);

    UOM createUOMRequestToUOM(CreateUOMRequest request);

    UOMResponse uomToUomResponse(UOM uom);

    UOM updateUOMRequestToUOM(UpdateUOMRequest request);
}

package com.amazonaws.saas.eks.product.mapper;

import com.amazonaws.saas.eks.product.dto.requests.uom.CreateUOMRequest;
import com.amazonaws.saas.eks.product.dto.requests.uom.UpdateUOMRequest;
import com.amazonaws.saas.eks.product.dto.responses.uom.UOMResponse;
import com.amazonaws.saas.eks.product.model.UOM;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UOMMapper {
    UOMMapper INSTANCE = Mappers.getMapper(UOMMapper.class);

    UOM createUOMRequestToUOM(CreateUOMRequest request);

    UOMResponse uomToUomResponse(UOM uom);

    UOM updateUOMRequestToUOM(UpdateUOMRequest request);

    List<UOMResponse> uomListToUOMResponseList(List<UOM> uomList);
}

package com.amazonaws.saas.eks.product.mapper;

import com.amazonaws.saas.eks.product.dto.requests.volumepricing.CreateVolumePricingRequest;
import com.amazonaws.saas.eks.product.dto.requests.volumepricing.UpdateVolumePricingRequest;
import com.amazonaws.saas.eks.product.dto.responses.volumepricing.VolumePricingResponse;
import com.amazonaws.saas.eks.product.model.VolumePricing;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VolumePricingMapper {
    VolumePricingMapper INSTANCE = Mappers.getMapper(VolumePricingMapper.class);

    VolumePricing createVolumePricingRequestToVolumePricing(CreateVolumePricingRequest request);

    VolumePricingResponse volumePricingToVolumePricingResponse(VolumePricing volumePricing);

    VolumePricing updateVolumePricingRequestToVolumePricing(UpdateVolumePricingRequest request);
}

package com.amazonaws.saas.eks.mapper;

import com.amazonaws.saas.eks.dto.requests.vendor.CreateVendorRequest;
import com.amazonaws.saas.eks.dto.requests.vendor.UpdateVendorRequest;
import com.amazonaws.saas.eks.dto.responses.vendor.VendorResponse;
import com.amazonaws.saas.eks.model.Vendor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VendorMapper {
    VendorMapper INSTANCE = Mappers.getMapper(VendorMapper.class);

    Vendor createVendorRequestToVendor(CreateVendorRequest request);

    VendorResponse vendorToVendorResponse(Vendor vendor);

    Vendor updateVendorRequestToVendor(UpdateVendorRequest request);
}

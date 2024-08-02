package com.amazonaws.saas.eks.product.mapper;

import com.amazonaws.saas.eks.product.dto.requests.vendor.CreateVendorRequest;
import com.amazonaws.saas.eks.product.dto.requests.vendor.UpdateVendorRequest;
import com.amazonaws.saas.eks.product.dto.responses.vendor.ListVendorItemResponse;
import com.amazonaws.saas.eks.product.dto.responses.vendor.ListVendorResponse;
import com.amazonaws.saas.eks.product.dto.responses.vendor.VendorResponse;
import com.amazonaws.saas.eks.product.model.vendor.Vendor;
import com.amazonaws.saas.eks.product.model.vendor.VendorSearchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface VendorMapper {
    VendorMapper INSTANCE = Mappers.getMapper(VendorMapper.class);

    Vendor createVendorRequestToVendor(CreateVendorRequest request);

    VendorResponse vendorToVendorResponse(Vendor vendor);

    Vendor updateVendorRequestToVendor(UpdateVendorRequest request);

    @Mapping(target = "phoneNumber", source = "phone1")
    @Mapping(target = "address", source = "physicalAddress")
    @Mapping(target = "city", source = "physicalCity")
    @Mapping(target = "state", source = "physicalState")
    @Mapping(target = "zip", source = "physicalZip")
    ListVendorItemResponse vendorToVendorListResponse(Vendor vendor);

    ListVendorResponse vendorSearchResponseToListVendorResponse(VendorSearchResponse response);
}

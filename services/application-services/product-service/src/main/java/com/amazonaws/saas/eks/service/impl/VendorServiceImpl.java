package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.dto.requests.vendor.CreateVendorRequest;
import com.amazonaws.saas.eks.dto.requests.vendor.ListVendorsRequestParams;
import com.amazonaws.saas.eks.dto.requests.vendor.UpdateVendorRequest;
import com.amazonaws.saas.eks.dto.responses.vendor.ListVendorResponse;
import com.amazonaws.saas.eks.dto.responses.vendor.VendorResponse;
import com.amazonaws.saas.eks.mapper.VendorMapper;
import com.amazonaws.saas.eks.model.Product;
import com.amazonaws.saas.eks.model.Vendor;
import com.amazonaws.saas.eks.repository.ProductRepository;
import com.amazonaws.saas.eks.repository.VendorRepository;
import com.amazonaws.saas.eks.service.VendorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class VendorServiceImpl implements VendorService {
    private static final Logger logger = LogManager.getLogger(VendorServiceImpl.class);

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public VendorResponse create(String tenantId, CreateVendorRequest request) {
        Vendor vendor = VendorMapper.INSTANCE.createVendorRequestToVendor(request);
        vendor.setCreated(new Date());
        vendor.setModified(vendor.getCreated());

        Vendor createdVendor = vendorRepository.insert(tenantId, vendor);
        return VendorMapper.INSTANCE.vendorToVendorResponse(createdVendor);
    }

    @Override
    public VendorResponse get(String tenantId, String id) {
        Vendor vendor = vendorRepository.get(tenantId, id);
        return VendorMapper.INSTANCE.vendorToVendorResponse(vendor);
    }

    @Override
    public VendorResponse update(String tenantId, String id, UpdateVendorRequest request) {
        Vendor vendor = VendorMapper.INSTANCE.updateVendorRequestToVendor(request);
        Vendor updatedVendor = vendorRepository.update(tenantId, id, vendor);
        updateVendorProducts(tenantId, updatedVendor);
        return VendorMapper.INSTANCE.vendorToVendorResponse(updatedVendor);
    }

    @Override
    public void delete(String tenantId, String id) {
        Vendor model = vendorRepository.get(tenantId, id);
        vendorRepository.delete(model);
    }

    @Override
    public ListVendorResponse getAll(String tenantId, ListVendorsRequestParams params) {
        ListVendorResponse response = new ListVendorResponse();
        List<Vendor> vendors = vendorRepository.getAll(tenantId, params.getFilter());
        for (Vendor v : vendors) {
            VendorResponse vr = VendorMapper.INSTANCE.vendorToVendorResponse(v);
            response.getVendors().add(vr);
        }

        return response;
    }

    private void updateVendorProducts(String tenantId, Vendor vendor) {
        List<Product> vendorProducts = productRepository.getVendorProducts(tenantId, vendor.getId());
        for (Product p : vendorProducts) {
            p.setVendorName(vendor.getName());
        }
        productRepository.batchUpdate(vendorProducts);
    }
}

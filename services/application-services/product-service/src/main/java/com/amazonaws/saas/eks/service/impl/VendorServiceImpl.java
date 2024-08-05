package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.exception.InvalidArgumentsException;
import com.amazonaws.saas.eks.exception.VendorNotFoundException;
import com.amazonaws.saas.eks.product.dto.requests.vendor.CreateVendorRequest;
import com.amazonaws.saas.eks.product.dto.requests.vendor.ListVendorsRequestParams;
import com.amazonaws.saas.eks.product.dto.requests.vendor.UpdateVendorRequest;
import com.amazonaws.saas.eks.product.dto.responses.vendor.ListVendorResponse;
import com.amazonaws.saas.eks.product.dto.responses.vendor.VendorResponse;
import com.amazonaws.saas.eks.product.mapper.VendorMapper;
import com.amazonaws.saas.eks.product.model.Product;
import com.amazonaws.saas.eks.product.model.vendor.Vendor;
import com.amazonaws.saas.eks.product.model.vendor.VendorSearchResponse;
import com.amazonaws.saas.eks.repository.ProductRepository;
import com.amazonaws.saas.eks.repository.VendorRepository;
import com.amazonaws.saas.eks.service.EncryptionService;
import com.amazonaws.saas.eks.service.VendorService;
import com.amazonaws.saas.eks.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class VendorServiceImpl implements VendorService {
    private static final Logger logger = LogManager.getLogger(VendorServiceImpl.class);

    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;
    private final EncryptionService encryptionService;

    public VendorServiceImpl(VendorRepository vendorRepository,
                             ProductRepository productRepository,
                             EncryptionService encryptionService) {
        this.vendorRepository = vendorRepository;
        this.productRepository = productRepository;
        this.encryptionService = encryptionService;
    }

    @Override
    public VendorResponse create(String tenantId, CreateVendorRequest request) {
        Vendor vendor = VendorMapper.INSTANCE.createVendorRequestToVendor(request);
        checkVendorNumber(tenantId, vendor);
        setPayeeVendor(tenantId, request.getPayeeVendorId(), vendor);
        encryptValues(vendor);
        Vendor createdVendor = vendorRepository.save(tenantId, vendor);
        return VendorMapper.INSTANCE.vendorToVendorResponse(createdVendor);
    }

    @Override
    public VendorResponse get(String tenantId, String id) {
        Vendor vendor = vendorRepository.get(tenantId, id).orElseThrow(() -> new VendorNotFoundException(id, Product.STORE_ID));
        decryptValues(vendor);
        return VendorMapper.INSTANCE.vendorToVendorResponse(vendor);
    }

    @Override
    public VendorResponse update(String tenantId, String id, UpdateVendorRequest request) {
        Vendor model = vendorRepository.get(tenantId, id).orElseThrow(() -> new VendorNotFoundException(id, Product.STORE_ID));
        Vendor vendor = VendorMapper.INSTANCE.updateVendorRequestToVendor(request);

        vendor.setId(id);
        vendor.setNumber(model.getNumber());
        checkVendorNumber(tenantId, vendor);
        setPayeeVendor(tenantId, request.getPayeeVendorId(), vendor);
        encryptValues(vendor);

        Vendor updatedVendor = vendorRepository.save(tenantId, vendor);
        if (!model.getName().equals(vendor.getName())) {
            updateProductVendorNames(tenantId, updatedVendor);
        }

        decryptValues(updatedVendor);
        return VendorMapper.INSTANCE.vendorToVendorResponse(updatedVendor);
    }

    @Override
    public void delete(String tenantId, String id) {
        Vendor model = vendorRepository.get(tenantId, id).orElseThrow(() -> new VendorNotFoundException(id, Product.STORE_ID));
        vendorRepository.delete(tenantId, model.getId());
    }

    @Override
    public ListVendorResponse getAll(String tenantId, ListVendorsRequestParams params) {
        VendorSearchResponse searchResponse = vendorRepository.findAll(tenantId, params.getFrom(), params.getSize(),
                params.getFilter(), params.getSortBy());
        return VendorMapper.INSTANCE.vendorSearchResponseToListVendorResponse(searchResponse);
    }

    private void updateProductVendorNames(String tenantId, Vendor vendor) {
        List<Product> vendorProducts = productRepository.getVendorProducts(tenantId, vendor.getId());
        List<CompletableFuture<Product>> futures = new ArrayList<>();
        for (Product p : vendorProducts) {
            p.setVendorName(vendor.getName());
            futures.add(CompletableFuture.supplyAsync(() -> productRepository.update(tenantId, p)));
        }
        Utils.completeFutures(futures);
    }

    private void setPayeeVendor(String tenantId, String payeeVendorId, Vendor vendor) {
        if (StringUtils.hasLength(payeeVendorId)) {
            Vendor payeeVendor = vendorRepository.get(tenantId, payeeVendorId)
                    .orElseThrow(() -> new VendorNotFoundException(payeeVendorId, Product.STORE_ID));
            vendor.setPayeeVendorId(payeeVendor.getId());
        }
    }

    private void encryptValues(Vendor vendor) {
        if (vendor.getEdi() != null && StringUtils.hasLength(vendor.getEdi().getPassword())) {
            try {
                vendor.getEdi().setPassword(encryptionService.encrypt(vendor.getEdi().getPassword()));
            } catch (Exception e) {
                logger.error("Error encrypting password", e);
                throw new RuntimeException("Error encrypting password");
            }
        }
    }

    private void decryptValues(Vendor vendor) {
        if (vendor.getEdi() != null && StringUtils.hasLength(vendor.getEdi().getPassword())) {
            try {
                vendor.getEdi().setPassword(encryptionService.decrypt(vendor.getEdi().getPassword()));
            } catch (Exception e) {
                logger.error("Error decrypting password", e);
                throw new RuntimeException("Error decrypting password");
            }
        }
    }

    private void checkVendorNumber(String tenantId, Vendor vendor) {
        List<Vendor> duplicateVendors = vendorRepository.getByNumber(tenantId, vendor.getNumber());
        if (duplicateVendors.stream().anyMatch(v -> !v.getId().equals(vendor.getId()))) {
            throw new InvalidArgumentsException("Vendor with number already exists");
        }
    }
}

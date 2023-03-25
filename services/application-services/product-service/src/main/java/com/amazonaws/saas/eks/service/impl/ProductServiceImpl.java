package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.dto.requests.product.CreateProductRequest;
import com.amazonaws.saas.eks.dto.requests.product.ListProductRequestParams;
import com.amazonaws.saas.eks.dto.requests.product.UpdateProductRequest;
import com.amazonaws.saas.eks.dto.responses.product.ListProductResponse;
import com.amazonaws.saas.eks.dto.responses.product.ProductResponse;
import com.amazonaws.saas.eks.exception.InvalidUOMNameException;
import com.amazonaws.saas.eks.mapper.ProductMapper;
import com.amazonaws.saas.eks.mapper.UOMMapper;
import com.amazonaws.saas.eks.model.*;
import com.amazonaws.saas.eks.repository.*;
import com.amazonaws.saas.eks.service.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    private static final int DEFAULT_SEARCH_START = 0;
    private static final int DEFAULT_SEARCH_SIZE = 10;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private UOMRepository uomRepository;

    @Autowired
    private VolumePricingRepository volumePricingRepository;

    @Override
    public ProductResponse create(String tenantId, CreateProductRequest request) {
        productRepository.checkProductExistence(tenantId, request.getSku());

        Product product = ProductMapper.INSTANCE.createProductRequestToProduct(request);
        product.setId(buildId());
        product.setCreated(new Date());
        product.setModified(product.getCreated());

        Category c = categoryRepository.getBaseCategory(tenantId, request.getCategoryId());
        product.setCategoryPath(c.getCategoryPath());
        product.setCategoryName(c.getName());

        if (!StringUtils.isEmpty(request.getVendorId())) {
            Vendor v = vendorRepository.get(tenantId, request.getVendorId());
            product.setVendorName(v.getName());
        }

        UOM uom = UOMMapper.INSTANCE.createUOMRequestToUOM(request.getUom());
        checkUomName(tenantId, uom.getName());
        uom.setProductId(product.getId());
        UOM uomModel = uomRepository.insert(tenantId, uom);
        product.setStockingUomId(uomModel.getId());
        product.setQuantityUomId(uomModel.getId());
        product.setPricingUomId(uomModel.getId());

        Product createdProduct = productRepository.insert(tenantId, product);

        mapUOMToProduct(tenantId, createdProduct);
        return ProductMapper.INSTANCE.productToProductResponse(createdProduct);
    }

    @Override
    public ListProductResponse getAll(String tenantId, ListProductRequestParams params) {
        ListProductResponse response = new ListProductResponse();

        List<Product> products;
        long count = 0;
        if (!StringUtils.isEmpty(params.getBarcode())) {
            UOM uom = uomRepository.getByBarcode(tenantId, params.getBarcode());
            Product p =  productRepository.get(tenantId, uom.getProductId());
            products = new ArrayList<>();
            products.add(p);
        } else {
            int from = params.getFrom() == null ? DEFAULT_SEARCH_START : params.getFrom();
            int size = params.getSize() == null ? DEFAULT_SEARCH_SIZE : params.getSize();
            ProductSearchResponse searchResponse = productRepository.search(tenantId, from, size, params.getCategoryId(), params.getVendorId(),
                    params.getFilter(), params.getSortBy());
            products = searchResponse.getProducts();
            count = searchResponse.getCount();
        }

        for (Product p: products) {
            ProductResponse pr = ProductMapper.INSTANCE.productToProductResponse(p);
            response.getProducts().add(pr);
        }
        response.setCount(count);
        return response;
    }

    @Override
    public ProductResponse get(String tenantId, String id) {
        Product product = productRepository.get(tenantId, id);
        mapUOMToProduct(tenantId, product);
        return ProductMapper.INSTANCE.productToProductResponse(product);
    }

    @Override
    public ProductResponse update(String tenantId, String id, UpdateProductRequest request) {
        Product product = ProductMapper.INSTANCE.updateProductRequestToProduct(request);
        product.setId(id);

        if (!StringUtils.isEmpty(request.getCategoryId())) {
            Category c = categoryRepository.getBaseCategory(tenantId, request.getCategoryId());
            product.setCategoryName(c.getName());
            product.setCategoryPath(c.getCategoryPath());
        }

        if (!StringUtils.isEmpty(request.getVendorId())) {
            Vendor v = vendorRepository.get(tenantId, request.getVendorId());
            product.setVendorName(v.getName());
        }

        if (!StringUtils.isEmpty(request.getStockingUomId())) {
            UOM u = uomRepository.get(tenantId, request.getStockingUomId());
            product.setStockingUomId(u.getId());
        }

        if (!StringUtils.isEmpty(request.getQuantityUomId())) {
            UOM u = uomRepository.get(tenantId, request.getQuantityUomId());
            product.setQuantityUomId(u.getId());
        }

        if (!StringUtils.isEmpty(request.getPricingUomId())) {
            UOM u = uomRepository.get(tenantId, request.getPricingUomId());
            product.setPricingUomId(u.getId());
        }

        Product updatedProduct = productRepository.update(tenantId, product);

        mapUOMToProduct(tenantId, updatedProduct);

        // If Product retail price changes, update VolumePricing price.
        if (product.getRetailPrice() != null && product.getRetailPrice().compareTo(BigDecimal.ZERO) >= 0) {
            volumePricingRepository.updateOnProductRetailPriceChange(tenantId, updatedProduct);
        }

        return ProductMapper.INSTANCE.productToProductResponse(updatedProduct);
    }

    @Override
    public void delete(String tenantId, String id) {
        productRepository.delete(tenantId, id);
    }

    private String buildId() {
        return String.valueOf(UUID.randomUUID());
    }

    private void checkUomName(String tenantId, String name) {
        Settings settings = settingsRepository.get(tenantId);
        if (!settings.getUnitOfMeasureNames().contains(name)) {
            throw new InvalidUOMNameException(tenantId, name);
        }
    }

    private void mapUOMToProduct(String tenantId, Product createdProduct) {
        List<UOM> productUOM = uomRepository.getUOMbyProductId(tenantId, createdProduct.getId());
        createdProduct.setUnitsOfMeasure(productUOM);
    }
}

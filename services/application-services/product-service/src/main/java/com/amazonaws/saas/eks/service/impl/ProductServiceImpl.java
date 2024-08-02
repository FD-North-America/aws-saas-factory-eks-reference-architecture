package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.exception.*;
import com.amazonaws.saas.eks.product.dto.requests.product.*;
import com.amazonaws.saas.eks.product.dto.responses.product.ListProductResponse;
import com.amazonaws.saas.eks.product.dto.responses.product.PricingResponse;
import com.amazonaws.saas.eks.product.dto.responses.product.ProductPricingResponse;
import com.amazonaws.saas.eks.product.dto.responses.product.ProductResponse;
import com.amazonaws.saas.eks.product.mapper.ProductMapper;
import com.amazonaws.saas.eks.product.mapper.UOMMapper;
import com.amazonaws.saas.eks.product.mapper.VolumePricingMapper;
import com.amazonaws.saas.eks.product.model.*;
import com.amazonaws.saas.eks.product.model.enums.EntityType;
import com.amazonaws.saas.eks.product.model.enums.ProductType;
import com.amazonaws.saas.eks.product.model.vendor.Vendor;
import com.amazonaws.saas.eks.repository.*;
import com.amazonaws.saas.eks.service.ProductService;
import com.amazonaws.saas.eks.settings.model.Settings;
import com.amazonaws.saas.eks.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private SalesHistoryRepository salesHistoryRepository;

    @Override
    public ProductResponse create(String tenantId, CreateProductRequest request) {
        // Generic Products can have SKUs of existing Normal Products
        if (!request.getType().equals(ProductType.GENERIC.toString())) {
            productRepository.checkProductExistence(tenantId, request.getSku());
        }

        Product product = ProductMapper.INSTANCE.createProductRequestToProduct(request);
        product.setId(buildId());
        product.setCreated(new Date());
        product.setModified(product.getCreated());

        Category c = categoryRepository.getBaseCategory(tenantId, request.getCategoryId());
        product.setCategoryPath(c.getCategoryPath());
        product.setCategoryName(c.getName());

        if (StringUtils.hasLength(request.getVendorId())) {
            Vendor v = vendorRepository.get(tenantId, request.getVendorId())
                    .orElseThrow(() -> new VendorNotFoundException(request.getVendorId(), Product.STORE_ID));
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
        if (StringUtils.hasLength(params.getBarcode())) {
            UOM uom = uomRepository.getByBarcode(tenantId, params.getBarcode());
            Product p =  productRepository.get(tenantId, uom.getProductId());
            products = new ArrayList<>();
            products.add(p);
        } else {
            int from = params.getFrom() == null ? DEFAULT_SEARCH_START : params.getFrom();
            int size = params.getSize() == null ? DEFAULT_SEARCH_SIZE : params.getSize();
            List<String> productIds = new ArrayList<>();
            if (StringUtils.hasLength(params.getFilter())) {
                List<UOM> matchingUom = uomRepository.searchByIdentifier(tenantId, params.getFilter());
                productIds = matchingUom.stream().map(UOM::getProductId).collect(Collectors.toList());
            }
            ProductSearchResponse searchResponse = productRepository.search(tenantId, from, size, params.getCategoryId(), params.getVendorId(),
                    params.getFilter(), params.getSortBy(), productIds);
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

        if (StringUtils.hasLength(request.getCategoryId())) {
            Category c = categoryRepository.getBaseCategory(tenantId, request.getCategoryId());
            product.setCategoryName(c.getName());
            product.setCategoryPath(c.getCategoryPath());
        }

        if (StringUtils.hasLength(request.getVendorId())) {
            Vendor v = vendorRepository.get(tenantId, request.getVendorId())
                    .orElseThrow(() -> new VendorNotFoundException(request.getVendorId(), Product.STORE_ID));
            product.setVendorName(v.getName());
        }

        if (StringUtils.hasLength(request.getStockingUomId())) {
            UOM u = uomRepository.get(tenantId, request.getStockingUomId());
            product.setStockingUomId(u.getId());
        }

        if (StringUtils.hasLength(request.getQuantityUomId())) {
            UOM u = uomRepository.get(tenantId, request.getQuantityUomId());
            product.setQuantityUomId(u.getId());
        }

        if (StringUtils.hasLength(request.getPricingUomId())) {
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

    @Override
    public PricingResponse getPricingDetails(String tenantId, PricingRequestParams params) {
        PricingResponse pricingResponse = new PricingResponse();

        // Find tax rate for tenant
        Settings settings = settingsRepository.get(tenantId);
        pricingResponse.setTaxRate(settings.getTaxRate());

        // Fetch products
        Map<String, ProductPricingRequest> productIdMap = constructPricingMap(tenantId, params.getProductPricingRequests());
        List<String> productIds = new ArrayList<>(productIdMap.keySet());
        List<Product> products = productRepository.batchLoad(tenantId, productIds);

        // Fetch Volume Pricing and UOM for each product
        Map<String, List<VolumePricing>> productVPMap = fetchVolumePricingMap(tenantId, productIds);;
        Map<String, List<UOM>> productUOMMap = fetchUOMMap(tenantId, productIds);;

        for (Product p : products) {
            // Set Product Values
            String productId = p.getId();
            ProductPricingRequest request = productIdMap.get(productId);
            ProductPricingResponse productResponse = ProductMapper.INSTANCE.productToProductPricingResponse(p);
            productResponse.setQuantity(request.getQuantity());

            // Set UOM Values
            if (productUOMMap.isEmpty() || !productUOMMap.containsKey(productId)) {
                throw new InvalidUOMProductIdException(tenantId, productId);
            }
            List<UOM> productUOMList = productUOMMap.get(productId);
            String uomId = StringUtils.hasLength(request.getUomId()) ? request.getUomId() : p.getPricingUomId();
            UOM selectedUOM = productUOMList.stream()
                    .filter(u -> u.getId().equals(uomId))
                    .findAny()
                    .orElseThrow(() -> new UOMNotFoundException(uomId, tenantId));
            productResponse.setUomId(uomId);
            productResponse.setUom(selectedUOM.getName());
            productResponse.setUomResponses(UOMMapper.INSTANCE.uomListToUOMResponseList(productUOMList));
            BigDecimal retailPrice = p.getRetailPrice() == null ? BigDecimal.ZERO : p.getRetailPrice();
            double uomFactor = selectedUOM.getFactor() == null ? 1.0 : selectedUOM.getFactor();
            productResponse.setRetailPrice(retailPrice.multiply(BigDecimal.valueOf(uomFactor)));
            productResponse.setCategoryId(p.getCategoryId());

            // Set VolumePricing Values
            if (!productVPMap.isEmpty() && productVPMap.containsKey(productId)) {
                for (VolumePricing vp : productVPMap.get(productId)) {
                    if (vp.getUomId().equals(uomId)) {
                        productResponse.getVolumePricing().add(VolumePricingMapper.INSTANCE.volumePricingToVolumePricingResponse(vp));
                    }
                }
            }

            pricingResponse.getProductPricing().putIfAbsent(productResponse.getId(), productResponse);
        }

        return pricingResponse;
    }

    @Override
    public void updateProductCounts(String tenantId, UpdateCountRequestParams params) {
        Map<String, UpdateProductCountRequest> requestMap = params.getProductCountRequests()
                .stream()
                .collect(Collectors.toMap(UpdateProductCountRequest::getId, u -> u));
        List<String> productIds = new ArrayList<>(requestMap.keySet());
        List<Product> products = productRepository.batchLoad(tenantId, productIds);
        List<SalesHistory> salesHistories = salesHistoryRepository.batchLoad(tenantId, productIds);
        String currentMonth = LocalDate.now().getMonth().toString();

        List<SalesHistory> salesHistoriesToUpdate = new ArrayList<>();
        for (Product p : products) {
            float newAmountSold = requestMap.get(p.getId()).getCount();
            p.setQuantityOnHand(p.getQuantityOnHand() - newAmountSold);

            String salesHistorySortKey = SalesHistory.buildSortKey(p.getId());
            Optional<SalesHistory> productHistory = salesHistories
                    .stream()
                    .filter(h -> h.getSortKey().equals(salesHistorySortKey))
                    .findFirst();

            if (productHistory.isPresent() && productHistory.get().getMonthAmountMap().containsKey(currentMonth)) {
                float currentSoldAmount = productHistory.get().getMonthAmountMap().get(currentMonth);
                productHistory.get().getMonthAmountMap().put(currentMonth, currentSoldAmount + newAmountSold);
                productHistory.get().setModified(new Date());
                salesHistoriesToUpdate.add(productHistory.get());
            } else {
                SalesHistory history = new SalesHistory();
                history.setCreated(new Date());
                history.setModified(history.getCreated());
                history.setPartitionKey(SalesHistory.buildPartitionKey(tenantId));
                history.setSortKey(salesHistorySortKey);
                history.getMonthAmountMap().put(currentMonth, newAmountSold);
                salesHistoriesToUpdate.add(history);
            }
        }

        productRepository.batchUpdate(tenantId, products);
        salesHistoryRepository.batchUpdate(tenantId, salesHistoriesToUpdate);
    }

    @Override
    public ListProductResponse getByIdentifier(String tenantId, String identifier) {
        ListProductResponse response = new ListProductResponse();
        List<Product> products;

        // Checking UOM for matching barcode or alternateIds
        List<UOM> uomList = uomRepository.searchByIdentifier(tenantId, identifier);
        if (uomList.isEmpty()) {
            // No matching barcodes or alternateIds? Checking SKU for any matches
            products = productRepository.searchByIdentifier(tenantId, identifier);
        } else {
            List<String> productIds = uomList.stream().map(UOM::getProductId).collect(Collectors.toList());
            products = productRepository.batchLoad(tenantId, productIds);
        }

        response.setProducts(ProductMapper.INSTANCE.productsToProductResponses(products));
        response.setCount(products.size());
        return response;
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
        List<UOM> productUOM = uomRepository.getByProductId(tenantId, createdProduct.getId());
        createdProduct.setUnitsOfMeasure(productUOM);
    }

    private Map<String, List<VolumePricing>> fetchVolumePricingMap(String tenantId, List<String> productIds) {
        // It's faster and more consistent to fetch the Volume Pricing by individual productIds
        // than querying the whole DynamoDB table
        List<CompletableFuture<List<VolumePricing>>> futures = new ArrayList<>();
        for (String productId : productIds) {
            futures.add(CompletableFuture.supplyAsync(() -> volumePricingRepository.getByProductId(tenantId, productId)));
        }

        Stream<VolumePricing> volumePricing;
        try {
            volumePricing = Utils.completeFutures(futures).get().stream().flatMap(Collection::stream);
        } catch (Exception e) {
            throw new BatchReadException(tenantId, EntityType.VOLUME_PRICING.getLabel());
        }

        return volumePricing
                .filter(VolumePricing::getActive)
                .collect(Collectors.groupingBy(VolumePricing::getProductId));
    }

    private Map<String, List<UOM>> fetchUOMMap(String tenantId, List<String> productIds) {
        // It's faster and more consistent to fetch the UOMs by individual productIds
        // than querying the whole DynamoDB table
        List<CompletableFuture<List<UOM>>> futures = new ArrayList<>();
        for (String productId : productIds) {
            futures.add(CompletableFuture.supplyAsync(() -> uomRepository.getByProductId(tenantId, productId)));
        }

        Stream<UOM> uomList;
        try {
            uomList = Utils.completeFutures(futures).get().stream().flatMap(Collection::stream);
        } catch (Exception e) {
            throw new BatchReadException(tenantId, EntityType.UOM.getLabel());
        }
        return uomList.collect(Collectors.groupingBy(UOM::getProductId));
    }

    /**
     * Constructs a Map using the productId as key. If only the barcode is present in the
     * request, then it will fetch the matching productId
     * @param tenantId
     * @param requests
     * @return Map with productId as key and ProductPricingRequest as value
     */
    private Map<String, ProductPricingRequest> constructPricingMap(String tenantId, List<ProductPricingRequest> requests) {
        Map<String, ProductPricingRequest> productIdMap = new HashMap<>();
        for (ProductPricingRequest r : requests) {
            if (!StringUtils.hasLength(r.getProductId())
                    && !StringUtils.hasLength(r.getBarcode())
                    && !StringUtils.hasLength(r.getSku())) {
                throw new InvalidProductPricingRequestException();
            }

            if (StringUtils.hasLength(r.getProductId())) {
                if (productIdMap.containsKey(r.getProductId())) {
                    productIdMap.get(r.getProductId()).setQuantity(r.getQuantity() + 1);
                } else {
                    productIdMap.put(r.getProductId(), r);
                }
            }

            // Fetching the correct productId if any requests contain barcodes
            if (StringUtils.hasLength(r.getBarcode())) {
                UOM u = uomRepository.getByBarcode(tenantId, r.getBarcode());
                r.setProductId(u.getProductId());
                if (productIdMap.containsKey(r.getProductId())) {
                    ProductPricingRequest existingRequest = productIdMap.get(r.getProductId());
                    int quantity = r.getQuantity() == null ? existingRequest.getQuantity() + 1 : existingRequest.getQuantity() + r.getQuantity();
                    existingRequest.setQuantity(quantity);
                } else {
                    productIdMap.put(r.getProductId(), r);
                }
            }

            if (StringUtils.hasLength(r.getSku())) {
                List<Product> products = productRepository.getProductBySKU(tenantId, r.getSku());
                Optional<Product> genericProduct = products
                        .stream()
                        .filter(p -> p.getType().equals(ProductType.GENERIC.toString()))
                        .findFirst();
                if (genericProduct.isPresent()) {
                    r.setProductId(genericProduct.get().getId());
                     if (!productIdMap.containsKey(genericProduct.get().getId())) {
                         productIdMap.put(genericProduct.get().getId(), r);
                     }
                }
            }

            // Making sure quantity is not null when being returned
            if (productIdMap.get(r.getProductId()).getQuantity() == null) {
                productIdMap.get(r.getProductId()).setQuantity(1);
            }
        }

        return productIdMap;
    }
}

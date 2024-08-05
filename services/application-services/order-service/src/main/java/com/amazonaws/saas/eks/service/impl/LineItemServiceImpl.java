package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.cashdrawer.dto.responses.CashDrawerResponse;
import com.amazonaws.saas.eks.clients.cashdrawer.CashDrawerServiceClient;
import com.amazonaws.saas.eks.clients.product.ProductServiceClient;
import com.amazonaws.saas.eks.exception.InvalidOrderArgumentsException;
import com.amazonaws.saas.eks.exception.InvalidProductPricingRequestException;
import com.amazonaws.saas.eks.exception.OrderUpdateStatusInvalidException;
import com.amazonaws.saas.eks.factory.LineItemProcessorFactory;
import com.amazonaws.saas.eks.order.dto.requests.ImportLineItemsRequest;
import com.amazonaws.saas.eks.order.dto.requests.LineItemRequest;
import com.amazonaws.saas.eks.order.dto.requests.UpdateLineItemsRequest;
import com.amazonaws.saas.eks.order.dto.requests.UpdateSingleLineItemRequest;
import com.amazonaws.saas.eks.order.mapper.OrderMapper;
import com.amazonaws.saas.eks.order.model.LineItem;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.order.model.enums.LineItemType;
import com.amazonaws.saas.eks.order.model.enums.OrderStatus;
import com.amazonaws.saas.eks.order.model.enums.OrderType;
import com.amazonaws.saas.eks.processors.lineitems.discounts.DiscountProcessorParams;
import com.amazonaws.saas.eks.processors.lineitems.generic.GenericProcessorParams;
import com.amazonaws.saas.eks.processors.lineitems.products.ProductProcessorParams;
import com.amazonaws.saas.eks.product.dto.requests.product.PricingRequestParams;
import com.amazonaws.saas.eks.product.dto.requests.product.ProductPricingRequest;
import com.amazonaws.saas.eks.product.dto.responses.product.PricingResponse;
import com.amazonaws.saas.eks.service.LineItemService;
import feign.FeignException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LineItemServiceImpl implements LineItemService {
    private static final Logger logger = LogManager.getLogger(LineItemServiceImpl.class);

    private static final List<String> PRODUCT_TYPES = Arrays.asList(LineItemType.PRODUCT.toString(),
            LineItemType.GENERIC.toString());
    private static final List<String> NON_GENERIC_TYPES = Arrays.asList(LineItemType.PRODUCT.toString(),
            LineItemType.DISCOUNT.toString());

    private final LineItemProcessorFactory lineItemProcessorFactory;
    private final ProductServiceClient productServiceClient;
    private final CashDrawerServiceClient cashDrawerServiceClient;

    public LineItemServiceImpl(LineItemProcessorFactory lineItemProcessorFactory,
                               ProductServiceClient productServiceClient,
                               CashDrawerServiceClient cashDrawerServiceClient) {
        this.lineItemProcessorFactory = lineItemProcessorFactory;
        this.productServiceClient = productServiceClient;
        this.cashDrawerServiceClient = cashDrawerServiceClient;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<LineItem> createLineItems(List<LineItemRequest> requests,
                                          Order order,
                                          CashDrawerResponse cashDrawer,
                                          String tenantId) {
        if (requests.isEmpty()) {
            return new ArrayList<>();
        }

        if (requests.stream().anyMatch(l -> l.getType() == null)) {
            throw new InvalidOrderArgumentsException("LineItem type cannot be null");
        }

        PricingResponse pricingResponse = new PricingResponse();

        // Get Pricing Details
        List<LineItemRequest> productRequests = requests
                .stream()
                .filter(l -> PRODUCT_TYPES.contains(l.getType()))
                .collect(Collectors.toList());
        if (!productRequests.isEmpty()) {
            pricingResponse = getPricingDetails(tenantId, productRequests);
        }

        List<LineItem> newLineItems = new ArrayList<>();
        Map<String, LineItem> genericItemMap = new HashMap<>();
        Map<String, Integer> genericIndexMap = new HashMap<>();
        var productProcessor = lineItemProcessorFactory.createProductLineItemProcessor();
        var discountProcessor = lineItemProcessorFactory.createDiscountLineItemProcessor();
        var genericProcessor = lineItemProcessorFactory.createGenericLineItemProcessor();
        for(int i = 0; i < requests.size(); i++) {
            LineItemRequest r = requests.get(i);
            switch (LineItemType.valueOfLabel(r.getType())) {
                case PRODUCT:
                    ProductProcessorParams params = ProductProcessorParams.builder()
                            .lineItems(newLineItems)
                            .pricing(pricingResponse)
                            .request(r)
                            .build();
                    newLineItems.add(productProcessor.process(params));
                    break;
                case DISCOUNT:
                    DiscountProcessorParams discountParams = DiscountProcessorParams.builder()
                            .tenantId(tenantId)
                            .request(r)
                            .order(order)
                            .lineItems(newLineItems)
                            .cashDrawerResponse(cashDrawer)
                            .build();
                    newLineItems.add(discountProcessor.process(discountParams));
                    break;
                case GENERIC:
                    GenericProcessorParams genericParams = GenericProcessorParams.builder()
                            .request(r)
                            .pricing(pricingResponse)
                            .lineItems(newLineItems)
                            .build();
                    LineItem newLineItem = genericProcessor.process(genericParams);
                    // For Generic Items, if they have the same attributes, they should be combined
                    if (newLineItem != null) {
                        String key = buildGenericKey(r);
                        if (genericItemMap.containsKey(key)) {
                            newLineItems.set(genericIndexMap.get(key), newLineItem);
                        } else {
                            genericItemMap.put(key, newLineItem);
                            genericIndexMap.put(key, i);
                            newLineItems.add(newLineItem);
                        }
                    }
                    break;
            }
        }

        return newLineItems;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<LineItem> updateLineItems(UpdateLineItemsRequest request, Order order, String tenantId) {
        if (!order.getStatus().equals(OrderStatus.PENDING.toString())) {
            throw new OrderUpdateStatusInvalidException(order.getId());
        }

        boolean containsGeneric = request.getLineItems().stream()
                .anyMatch(l -> l.getType().equals(LineItemType.GENERIC.toString()));
        if (request.getLineItems().size() == order.getLineItems().size() && !containsGeneric) {
            return sortLineItemsByRequestOrder(request.getLineItems(), order.getLineItems());
        }

        CashDrawerResponse cashDrawerResponse = cashDrawerServiceClient.get(tenantId, order.getCashDrawerId())
                .getBody();
        return createLineItems(request.getLineItems(), order, cashDrawerResponse, tenantId);
    }

    /**
     * @inheritDoc
     */
    @Override
    public LineItem update(UpdateSingleLineItemRequest request, LineItem lineItem, String tenantId) {
        // Fetch updated pricing details if UOM changes
        PricingResponse pricingResponse = new PricingResponse();
        if (needsNewPricingCalculations(lineItem, request)) {
            LineItemRequest pricingRequest = OrderMapper.INSTANCE.updateSingleLineItemToLineItemRequest(request);
            pricingResponse = getPricingDetails(tenantId, List.of(pricingRequest));
        }

        LineItem newLineItem = lineItem;
        switch (LineItemType.valueOfLabel(request.getType())) {
            case PRODUCT:
                var productProcessor = lineItemProcessorFactory.createProductLineItemProcessor();
                newLineItem = productProcessor.update(lineItem, request, pricingResponse);
                break;
            case GENERIC:
                var genericProcessor = lineItemProcessorFactory.createGenericLineItemProcessor();
                newLineItem = genericProcessor.update(lineItem, request, pricingResponse);
                break;
            case DISCOUNT:
                var discountProcessor = lineItemProcessorFactory.createDiscountLineItemProcessor();
                newLineItem = discountProcessor.update(lineItem, request, pricingResponse);
                break;
        }

        // Filling out any missing details
        if (newLineItem.getUomList() == null || newLineItem.getUomList().isEmpty()) {
            newLineItem.setUomList(lineItem.getUomList());
        }
        return newLineItem;
    }

    /**
     * Imports Line Items from one Order to another
     *
     * @param request      {@link ImportLineItemsRequest}
     * @param orderRequest Order Request
     * @param order        Order
     * @return List of Line Items
     */
    @Override
    public List<LineItem> getLineItemsToImport(ImportLineItemsRequest request, Order orderRequest, Order order) {
        List<LineItem> lineItems = orderRequest.getLineItems();

        if (request.getLineItems() != null && !request.getLineItems().isEmpty()) {
            // Building a map of generic items to check for duplicates
            Map<String, List<LineItemRequest>> genericItemRequestMap = request.getLineItems().stream()
                    .filter(l -> l.getType().equals(LineItemType.GENERIC.toString()))
                    .collect(Collectors.groupingBy(this::buildGenericKey));

            List<LineItem> filteredLineItems = new ArrayList<>();
            for (LineItem li : lineItems) {
                if (li.getType().equals(LineItemType.GENERIC.toString()) && !genericItemRequestMap.containsKey(buildGenericKey(li))) {
                    filteredLineItems.add(li);
                } else {
                    filteredLineItems.add(li);
                }
            }
            // For imports from invoice, set quantity to one
            if (orderRequest.getType().equals(OrderType.INVOICE.toString())) {
                filteredLineItems.forEach(li -> li.setQuantity(1));
                // Allow to import line items from multiple invoices
                filteredLineItems.addAll(order.getLineItems());
            }
            lineItems = filteredLineItems;
        }
        return lineItems;
    }

    private PricingResponse getPricingDetails(String tenantId, List<LineItemRequest> orderProducts) {
        PricingRequestParams request = new PricingRequestParams();
        for (LineItemRequest o : orderProducts) {
            ProductPricingRequest r = new ProductPricingRequest();
            r.setProductId(o.getId());
            r.setQuantity(o.getQuantity());
            r.setUomId(o.getUomId());
            r.setBarcode(o.getBarcode());
            r.setSku(o.getSku());
            request.getProductPricingRequests().add(r);
        }

        ResponseEntity<PricingResponse> response;
        try {
            response = productServiceClient.getPricingDetails(tenantId, request);
        } catch (FeignException e) {
            if (e.status() == HttpStatus.NOT_FOUND.value()) {
                throw new InvalidProductPricingRequestException();
            }
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Error getting response from Product Service");
        }
        return response.getBody();
    }

    private boolean needsNewPricingCalculations(LineItem currentLineItem, UpdateSingleLineItemRequest request) {
        return !Objects.equals(currentLineItem.getUomId(), request.getUomId())
                || !Objects.equals(currentLineItem.getQuantity(), request.getQuantity());
    }

    private List<LineItem> sortLineItemsByRequestOrder(List<LineItemRequest> requests, List<LineItem> lineItems) {
        // Splitting line items between Generic and Non-Generic because Generic items can have the same ID
        Map<String, LineItem> nonGenericItems = lineItems.stream()
                .filter(l -> NON_GENERIC_TYPES.contains(l.getType()))
                .collect(Collectors.toMap(LineItem::getId, Function.identity()));
        Map<String, LineItem> genericItems = lineItems.stream()
                .filter(l -> l.getType().equals(LineItemType.GENERIC.toString()))
                .collect(Collectors.toMap(this::buildGenericKey, Function.identity()));

        List<LineItem> updatedLineItems = new ArrayList<>();
        for (LineItemRequest requestLineItem : requests) {
            LineItem lineItemToAdd = nonGenericItems.get(requestLineItem.getId());
            if (lineItemToAdd != null) {
                addLineItemToSortedList(updatedLineItems, lineItemToAdd, requestLineItem.getQuantity());
            } else {
                String genericKey = buildGenericKey(requestLineItem);
                lineItemToAdd = genericItems.get(genericKey);
                if (lineItemToAdd != null) {
                    addLineItemToSortedList(updatedLineItems, lineItemToAdd, requestLineItem.getQuantity());
                }
            }
        }

        return updatedLineItems;
    }

    private void addLineItemToSortedList(List<LineItem> updatedLineItems, LineItem lineItemToAdd, int quantity) {
        lineItemToAdd.setExtendedPrice(lineItemToAdd.getPrice().multiply(BigDecimal.valueOf(quantity)));
        lineItemToAdd.setQuantity(quantity);
        updatedLineItems.add(lineItemToAdd);
    }

    private String buildGenericKey(LineItem lineItem) {
        return String.format("%s%s%s%s", lineItem.getId(), lineItem.getSku(), lineItem.getName(), lineItem.getPrice().toString());
    }

    private String buildGenericKey(LineItemRequest request) {
        String name = StringUtils.hasLength(request.getName()) ? request.getName() : request.getDescription();
        return String.format("%s%s%s%s", request.getId(), request.getSku(), name, request.getPrice().toString());
    }
}

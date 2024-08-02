package com.amazonaws.saas.eks.processors.lineitems.generic;

import com.amazonaws.saas.eks.exception.OrderException;
import com.amazonaws.saas.eks.mapper.ServiceMapper;
import com.amazonaws.saas.eks.order.dto.requests.LineItemRequest;
import com.amazonaws.saas.eks.order.dto.requests.UpdateSingleLineItemRequest;
import com.amazonaws.saas.eks.order.mapper.OrderMapper;
import com.amazonaws.saas.eks.order.model.LineItem;
import com.amazonaws.saas.eks.order.model.enums.LineItemType;
import com.amazonaws.saas.eks.processors.lineitems.LineItemProcessor;
import com.amazonaws.saas.eks.product.dto.responses.product.PricingResponse;
import com.amazonaws.saas.eks.product.dto.responses.product.ProductPricingResponse;
import com.amazonaws.saas.eks.product.model.enums.ProductTaxable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.amazonaws.saas.eks.utils.Utils.populateUOMList;
import static com.amazonaws.saas.eks.utils.Utils.roundValue;

@Service
public class GenericProcessor implements LineItemProcessor<GenericProcessorParams> {
    @Override
    public LineItem process(GenericProcessorParams parameters) {
        LineItemRequest request = parameters.getRequest();
        List<LineItem> lineItems = parameters.getLineItems();
        Map<String, ProductPricingResponse> skuMap = buildSkuPricingMap(parameters.getPricing());
        if (!isValidGenericItem(request)) {
            throw new OrderException("Invalid generic item in line items");
        }
        if (request.getCreated() == null) {
            request.setCreated(new Date());
        }
        LineItem l = ServiceMapper.INSTANCE.lineItemRequestToLineItem(request);
        l.setName(request.getDescription());
        l.setQuantity(request.getQuantity() == null ? 1 : request.getQuantity());
        if (skuMap.containsKey(request.getSku())) {
            ProductPricingResponse pr = skuMap.get(request.getSku());
            l.setId(pr.getId());
            l.setTaxable(pr.getTaxable().equals(ProductTaxable.TAXABLE.toString()));
            l.setUom(pr.getUom());
            l.setUomId(pr.getUomId());
            l.setCost(pr.getCost() == null ? BigDecimal.ZERO : pr.getCost());
            l.setPrice(roundValue(l.getPrice()));
            l.setReturnsAllowed(pr.isReturnsAllowed());
            l.setShipped(l.getQuantity());
            l.setBackOrdered(l.getQuantity() - l.getShipped());
            l.setCategoryId(pr.getCategoryId());
            populateUOMList(pr, l);
        }
        Optional<LineItem> currentGenericLineItem = findMatchingGenericItem(lineItems, request);
        if (currentGenericLineItem.isEmpty()) {
            l.setExtendedPrice(l.getPrice().multiply(BigDecimal.valueOf(l.getQuantity())));
        } else {
            LineItem currentItem = currentGenericLineItem.get();
            l.setQuantity(currentItem.getQuantity() + l.getQuantity());
            l.setShipped(l.getQuantity());
            l.setExtendedPrice(currentItem.getPrice().multiply(BigDecimal.valueOf(l.getQuantity())));
        }

        calculateTaxAmount(l, parameters.getPricing());

        return l;
    }

    private static void calculateTaxAmount(LineItem l, PricingResponse pricing) {
        if (l.getTaxable()) {
            l.setTaxAmount(roundValue(l.getExtendedPrice().multiply(pricing.getTaxRate())));
        }
    }

    @Override
    public LineItem update(LineItem lineItem, UpdateSingleLineItemRequest request, PricingResponse pricingResponse) {
        LineItem newLineItem;
        if (pricingResponse.getProductPricing().containsKey(lineItem.getId())) {
            ProductPricingResponse pr = pricingResponse.getProductPricing().get(lineItem.getId());
            if (!lineItem.getPrice().equals(request.getPrice())) {
                // Override the price from the incoming request if they're different
                pr.setRetailPrice(request.getPrice());
            }
            newLineItem = ServiceMapper.INSTANCE.lineItemFromUpdateRequestAndPricing(request, pr);
            populateUOMList(pr, newLineItem);
        } else {
            newLineItem = OrderMapper.INSTANCE.updateSingleLineItemRequestToLineItem(request, lineItem);
        }

        newLineItem.setExtendedPrice(roundValue(newLineItem.getPrice().multiply(BigDecimal.valueOf(newLineItem.getQuantity()))));
        calculateTaxAmount(newLineItem, pricingResponse);
        return newLineItem;
    }

    private boolean isValidGenericItem(LineItemRequest request) {
        return request.getType().equals(LineItemType.GENERIC.toString())
                && StringUtils.hasLength(request.getSku())
                && StringUtils.hasLength(request.getDescription())
                && request.getPrice() != null;
    }

    private Optional<LineItem> findMatchingGenericItem(List<LineItem> lineItems, LineItemRequest r) {
        return lineItems
                .stream()
                .filter(l -> l.getType().equals(LineItemType.GENERIC.toString())
                        && l.getSku().equals(r.getSku())
                        && l.getPrice().equals(roundValue(r.getPrice()))
                        && l.getDescription().equals(r.getDescription()))
                .findAny();
    }

    private Map<String, ProductPricingResponse> buildSkuPricingMap(PricingResponse pricingResponse) {
        Map<String, ProductPricingResponse> skuMap = new HashMap<>();
        for (ProductPricingResponse pr : pricingResponse.getProductPricing().values()) {
            skuMap.putIfAbsent(pr.getSku(), pr);
        }
        return skuMap;
    }
}

package com.amazonaws.saas.eks.processors.lineitems.products;

import com.amazonaws.saas.eks.mapper.ServiceMapper;
import com.amazonaws.saas.eks.order.dto.requests.UpdateSingleLineItemRequest;
import com.amazonaws.saas.eks.order.mapper.OrderMapper;
import com.amazonaws.saas.eks.order.model.LineItem;
import com.amazonaws.saas.eks.order.model.enums.LineItemType;
import com.amazonaws.saas.eks.processors.lineitems.LineItemProcessor;
import com.amazonaws.saas.eks.product.dto.responses.product.PricingResponse;
import com.amazonaws.saas.eks.product.dto.responses.product.ProductPricingResponse;
import com.amazonaws.saas.eks.product.dto.responses.volumepricing.VolumePricingResponse;
import com.amazonaws.saas.eks.product.model.enums.ProductTaxable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.amazonaws.saas.eks.utils.Utils.populateUOMList;
import static com.amazonaws.saas.eks.utils.Utils.roundValue;

@Service
public class ProductProcessor implements LineItemProcessor<ProductProcessorParams> {
    @Override
    public LineItem process(ProductProcessorParams parameters) {
        Map<String, LineItem> productIdMap = buildLineItemMap(parameters.getLineItems());
        String productId = parameters.getRequest().getId();
        PricingResponse pricing = parameters.getPricing();
        ProductPricingResponse p = pricing.getProductPricing().get(productId);

        LineItem l = ServiceMapper.INSTANCE.productPricingResponseToLineItem(p);
        l.setTaxable(p.getTaxable().equals(ProductTaxable.TAXABLE.toString()));
        l.setType(LineItemType.PRODUCT.toString());
        l.setQuantity(p.getQuantity());
        l.setShipped(l.getQuantity());
        l.setBackOrdered(l.getQuantity() - l.getShipped());

        populateProductDetails(p, l, pricing);
        if (productIdMap.containsKey(productId)) {
            l.setCreated(productIdMap.get(productId).getCreated());
        } else {
            l.setCreated(new Date());
        }

        l.setExtendedPrice(roundValue(l.getPrice().multiply(BigDecimal.valueOf(l.getQuantity()))));

        return l;
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
            populateProductDetails(pr, newLineItem, pricingResponse);
        } else {
            newLineItem = OrderMapper.INSTANCE.updateSingleLineItemRequestToLineItem(request, lineItem);
        }

        return newLineItem;
    }

    private static void populateProductDetails(ProductPricingResponse p, LineItem l, PricingResponse pricing) {
        populateUOMList(p, l);
        calculatePrice(p, l);
        if (l.getTaxable()) {
            l.setTaxAmount(roundValue(l.getExtendedPrice().multiply(pricing.getTaxRate())));
        }
    }

    private static void calculatePrice(ProductPricingResponse p, LineItem l) {
        int highestBreakpoint = 0;
        BigDecimal volumePrice = BigDecimal.ZERO;

        // Looping through the volume pricing and making sure we get the price from the highest breakpoint
        for (VolumePricingResponse vp : p.getVolumePricing()) {
            if (p.getQuantity() >= vp.getBreakPointQty() && vp.getBreakPointQty() >= highestBreakpoint) {
                volumePrice = roundValue(vp.getPrice());
                highestBreakpoint = vp.getBreakPointQty();
            }
        }

        if (volumePrice.compareTo(BigDecimal.ZERO) > 0) {
            l.setPrice(volumePrice);
        } else {
            BigDecimal price = p.getRetailPrice() == null  ? BigDecimal.ZERO : p.getRetailPrice();
            l.setPrice(roundValue(price));
        }

        l.setExtendedPrice(roundValue(l.getPrice().multiply(BigDecimal.valueOf(l.getQuantity()))));
    }

    private Map<String, LineItem> buildLineItemMap(List<LineItem> lineItems) {
        return lineItems.stream()
                .filter(l -> l.getType().equals(LineItemType.PRODUCT.toString()))
                .collect(Collectors.toMap(LineItem::getId, l -> l));
    }
}

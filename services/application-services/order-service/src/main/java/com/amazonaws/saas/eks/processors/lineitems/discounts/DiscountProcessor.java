package com.amazonaws.saas.eks.processors.lineitems.discounts;

import com.amazonaws.saas.eks.cashdrawer.dto.responses.CashDrawerResponse;
import com.amazonaws.saas.eks.exception.OrderException;
import com.amazonaws.saas.eks.order.dto.requests.LineItemRequest;
import com.amazonaws.saas.eks.order.dto.requests.UpdateSingleLineItemRequest;
import com.amazonaws.saas.eks.order.mapper.OrderMapper;
import com.amazonaws.saas.eks.order.model.Discount;
import com.amazonaws.saas.eks.order.model.LineItem;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.order.model.enums.DiscountType;
import com.amazonaws.saas.eks.order.model.enums.LineItemType;
import com.amazonaws.saas.eks.processors.lineitems.LineItemProcessor;
import com.amazonaws.saas.eks.product.dto.responses.product.PricingResponse;
import com.amazonaws.saas.eks.repository.DiscountRepository;
import com.amazonaws.saas.eks.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DiscountProcessor implements LineItemProcessor<DiscountProcessorParams> {

    private static final String DISCOUNT_NAME = "DISCOUNT";

    @Autowired
    private DiscountRepository discountRepository;

    @Override
    public LineItem process(DiscountProcessorParams parameters) {
        Order order = parameters.getOrder();
        CashDrawerResponse cashDrawerResponse = parameters.getCashDrawerResponse();
        String tenantId = parameters.getTenantId();
        LineItemRequest request = parameters.getRequest();

        Map<String, LineItem> discountIdMap = buildLineItemMap(parameters.getLineItems());

        Discount discount;
        if (!StringUtils.hasLength(request.getId()) && discountIdMap.containsKey(request.getId())) {
            discount = discountRepository.get(request.getId(), tenantId);
        } else {
            discount = new Discount();
            discount.setOrderId(order.getId());
            discount.setOrderNumber(order.getNumber());
            discount.setRepUser(cashDrawerResponse.getAssignedUser());
            discount.setCode("MFG");
        }

        discount.setType(request.getDiscountType());
        discount.setReason(request.getDiscountReason());
        discount.setPrice(Utils.roundValue(request.getPrice()));


        List<LineItem> productLineItems = order.getLineItems().stream()
                .filter(l -> !l.getType().equals(LineItemType.DISCOUNT.toString()))
                .collect(Collectors.toList());
        if (productLineItems.isEmpty()) {
            throw new OrderException("no line items to discount");
        }
        LineItem lastLineItem = productLineItems.get(productLineItems.size() - 1);
        if (lastLineItem.getDiscount() == null || !lastLineItem.getDiscount()) {
            throw new OrderException(String.format("line item %s not marked as discountable", lastLineItem.getName()));
        }

        if (discount.getType().equals(DiscountType.DOLLAR.toString())) {
            discount.setPrice(Utils.roundValue(request.getPrice()));
        } else if (discount.getType().equals(DiscountType.PERCENTAGE.toString())) {
            BigDecimal discountPrice = lastLineItem.getExtendedPrice().multiply(request.getPrice());
            discount.setPrice(Utils.roundValue(discountPrice));
        }
        Discount updatedDiscount = discountRepository.save(discount, tenantId);
        LineItem discountLineItem = OrderMapper.INSTANCE.discountToLineItem(updatedDiscount);
        discountLineItem.setType(LineItemType.DISCOUNT.toString());
        discountLineItem.setQuantity(1);


        return discountLineItem;
    }

    @Override
    public LineItem update(LineItem lineItem, UpdateSingleLineItemRequest request, PricingResponse pricingResponse) {
        return OrderMapper.INSTANCE.updateSingleLineItemRequestToLineItem(request, lineItem);
    }

    private Map<String, LineItem> buildLineItemMap(List<LineItem> lineItems) {
        return lineItems.stream()
                .filter(l -> l.getType().equals(LineItemType.DISCOUNT.toString()))
                .collect(Collectors.toMap(LineItem::getId, l -> l));
    }
}

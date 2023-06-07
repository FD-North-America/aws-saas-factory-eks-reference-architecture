package com.amazonaws.saas.eks.mapper;

import com.amazonaws.saas.eks.clients.product.dto.responses.ProductPricingResponse;
import com.amazonaws.saas.eks.dto.requests.orders.LineItemRequest;
import com.amazonaws.saas.eks.dto.requests.orders.UpdateOrderRequest;
import com.amazonaws.saas.eks.dto.responses.TransactionResponse;
import com.amazonaws.saas.eks.dto.responses.orders.LineItemResponse;
import com.amazonaws.saas.eks.dto.responses.orders.OrderResponse;
import com.amazonaws.saas.eks.model.Discount;
import com.amazonaws.saas.eks.model.LineItem;
import com.amazonaws.saas.eks.model.Order;
import com.amazonaws.saas.eks.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderResponse orderToOrderResponse(Order order);

    Order updateOrderRequestToOrder(UpdateOrderRequest request);

    LineItemResponse lineItemToLineItemResponse(LineItem lineItem);

    LineItem lineItemRequestToLineItem(LineItemRequest lineItemRequest);

    LineItem productPricingResponseToLineItem(ProductPricingResponse response);

    LineItem discountToLineItem(Discount discount);

    TransactionResponse transactionToTransactionResponse(Transaction transaction);
}

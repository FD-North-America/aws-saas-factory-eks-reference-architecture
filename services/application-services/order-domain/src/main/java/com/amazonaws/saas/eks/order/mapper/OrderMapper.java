package com.amazonaws.saas.eks.order.mapper;

import com.amazonaws.saas.eks.order.dto.requests.LineItemRequest;
import com.amazonaws.saas.eks.order.dto.requests.PaidOutCodeRequest;
import com.amazonaws.saas.eks.order.dto.requests.UpdateOrderRequest;
import com.amazonaws.saas.eks.order.dto.requests.UpdateSingleLineItemRequest;
import com.amazonaws.saas.eks.order.dto.requests.reasoncodes.ReasonCodeItemRequest;
import com.amazonaws.saas.eks.order.dto.responses.*;
import com.amazonaws.saas.eks.order.dto.responses.reasoncodes.ReasonCodeItemResponse;
import com.amazonaws.saas.eks.order.model.*;
import com.amazonaws.saas.eks.order.model.search.OrderSearchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderResponse orderToOrderResponse(Order order);

    Order updateOrderRequestToOrder(UpdateOrderRequest request);

    LineItemResponse lineItemToLineItemResponse(LineItem lineItem);

    LineItem lineItemRequestToLineItem(LineItemRequest lineItemRequest);

    List<LineItemRequest> lineItemsToLineItemRequest(List<LineItem> lineItems);

    @Mapping(source = "discount.reason", target = "name")
    @Mapping(source = "discount.price", target = "extendedPrice")
    LineItem discountToLineItem(Discount discount);

    TransactionResponse transactionToTransactionResponse(Transaction transaction);

    PaidOutCodeResponse paidOutCodeToPaidOutCodeResponse(PaidOutCode paidOutCode);

    PaidOutCode paidOutCodeRequestToPaidOutCode(PaidOutCodeRequest paidOutCodeRequest);

    PaidOutCodeItem paidOutCodeToPaidOutCodeItem(PaidOutCode paidOutCode);

    ReasonCodeItemResponse reasonCodeItemToReasonCodeItemResponse(ReasonCodeItem reasonCode);

    PaidOutCodeResponse paidOutCodeItemToPaidOutCodeResponse(PaidOutCodeItem paidOutCodeItem);

    @Mapping(source = "lineItem.id", target = "id")
    @Mapping(source = "request.price", target = "price")
    @Mapping(source = "request.quantity", target = "quantity")
    @Mapping(source = "lineItem.sku", target = "sku")
    @Mapping(source = "lineItem.created", target = "created")
    @Mapping(source = "lineItem.type", target = "type")
    @Mapping(source = "request.description", target = "description")
    @Mapping(source = "request.uomId", target = "uomId")
    @Mapping(source = "request.shipped", target = "shipped")
    @Mapping(source = "request.backOrdered", target = "backOrdered")
    @Mapping(source = "request.pickupOrLoad", target = "pickupOrLoad")
    @Mapping(source = "request.discount", target = "discount")
    @Mapping(source = "request.taxable", target = "taxable")
    LineItem updateSingleLineItemRequestToLineItem(UpdateSingleLineItemRequest request, LineItem lineItem);

    LineItemRequest updateSingleLineItemToLineItemRequest(UpdateSingleLineItemRequest lineItem);

    @Mapping(source = "order.created", target = "date")
    @Mapping(source = "order.balanceDue", target = "amountDue")
    OrderRowResponse orderToOrderRowResponse(Order order);

    ListOrdersResponse orderSearchResponseToListOrdersResponse(OrderSearchResponse response);
}

package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.cashdrawer.dto.responses.CashDrawerResponse;
import com.amazonaws.saas.eks.order.dto.requests.ImportLineItemsRequest;
import com.amazonaws.saas.eks.order.dto.requests.LineItemRequest;
import com.amazonaws.saas.eks.order.dto.requests.UpdateLineItemsRequest;
import com.amazonaws.saas.eks.order.dto.requests.UpdateSingleLineItemRequest;
import com.amazonaws.saas.eks.order.model.LineItem;
import com.amazonaws.saas.eks.order.model.Order;

import java.util.List;

public interface LineItemService {
    /**
     * Creates a list of Line Items for an Order
     * @param requests Line Item Requests
     * @param order Order
     * @param cashDrawer Cash Drawer
     * @param tenantId Tenant ID
     * @return List of Line Items
     */
    List<LineItem> createLineItems(List<LineItemRequest> requests,
                                   Order order,
                                   CashDrawerResponse cashDrawer,
                                   String tenantId);

    /**
     * Updates the whole list of Line Items for an Order
     * @param request {@link UpdateLineItemsRequest}
     * @param tenantId Tenant ID
     * @param order Order
     * @return List of Line Items
     */
    List<LineItem> updateLineItems(UpdateLineItemsRequest request, Order order, String tenantId);

    /**
     * Updates a single line item in the Order
     * @param request {@link UpdateSingleLineItemRequest}
     * @param tenantId Tenant ID
     * @param lineItem Line Item ID
     * @return {@link LineItem}
     */
    LineItem update(UpdateSingleLineItemRequest request, LineItem lineItem, String tenantId);

    /**
     * Imports Line Items from one Order to another
     * @param request {@link ImportLineItemsRequest}
     * @param orderRequest Order Request
     * @param order Order
     * @return List of Line Items
     */
    List<LineItem> getLineItemsToImport(ImportLineItemsRequest request,
                                        Order orderRequest,
                                        Order order);
}

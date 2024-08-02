package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.order.dto.requests.*;
import com.amazonaws.saas.eks.order.dto.requests.itemsinfo.ItemsInfoRequest;
import com.amazonaws.saas.eks.order.dto.responses.ChargeCodeListResponse;
import com.amazonaws.saas.eks.order.dto.responses.ListOrdersResponse;
import com.amazonaws.saas.eks.order.dto.responses.OrderResponse;
import com.amazonaws.saas.eks.order.dto.responses.OrdersByCashDrawerResponse;
import com.amazonaws.saas.eks.order.dto.responses.itemsinfo.ItemsInfoResponse;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.http.ResponseEntity;

public interface OrderService {

	/**
	 * Creates a new Order
	 * @param request {@link CreateOrderRequest}
	 * @param tenantId Tenant ID
	 * @param username User creating the order
	 * @return {@link OrderResponse}
	 */
	OrderResponse create(CreateOrderRequest request, String tenantId, String username);

	/**
	 * Soft deletes an Order
	 * @param orderId Order ID
	 * @param tenantId Tenant ID
	 */
	void delete(String orderId, String tenantId);

	/**
	 * Updates an Order
	 * @param orderId Order ID
	 * @param tenantId Tenant ID
	 * @param request {@link UpdateOrderRequest}
	 * @return {@link OrderResponse}
	 */
	OrderResponse update(String orderId, String tenantId, UpdateOrderRequest request);

	/**
	 * Retrieves a single Order by ID
	 * @param orderId Order ID
	 * @param tenantId Tenant ID
	 * @return {@link OrderResponse}
	 */
	OrderResponse get(String orderId, String tenantId);

	/**
	 * Applies a Patch to an Order. See <a href="https://www.baeldung.com/spring-rest-json-patch">documentation</a>
	 * @param orderId Order ID
	 * @param patch {@link JsonPatch}
	 * @param tenantId Tenant ID
	 * @return {@link OrderResponse}
	 */
	OrderResponse patch(String orderId, JsonPatch patch, String tenantId);

	/**
	 * Searches for Orders based on the given parameters
	 * @param request {@link OrderSearchRequest}
	 * @param tenantId Tenant ID
	 * @return {@link ListOrdersResponse}
	 */
	ListOrdersResponse search(OrderSearchRequest request, String tenantId);


	/**
	 * Finalizes the Order and converts it to an Invoice
	 * @param orderId Order ID
	 * @param tenantId Tenant ID
	 * @return {@link OrderResponse}
	 */
	OrderResponse finalize(String orderId, String tenantId);

	/**
	 * Updates the Order type to either Sales or Quote
	 * @param request {@link UpdateOrderTypeRequest}
	 * @param orderId Order ID
	 * @param tenantId Tenant ID
	 * @return {@link OrderResponse}
	 */
	OrderResponse saveType(UpdateOrderTypeRequest request, String orderId, String tenantId);

	/**
	 * Imports Line Items from one Order to another
	 * @param orderId Order ID
	 * @param tenantId Tenant ID
	 * @param request {@link ImportLineItemsRequest}
	 * @return {@link OrderResponse}
	 */
	OrderResponse importLineItems(String orderId, String tenantId, ImportLineItemsRequest request);

	/**
	 * Updates the whole list of Line Items for an Order
	 * @param request {@link UpdateLineItemsRequest}
	 * @param tenantId Tenant ID
	 * @param orderId Order ID
	 * @return {@link OrderResponse}
	 */
	OrderResponse updateLineItems(UpdateLineItemsRequest request, String tenantId, String orderId);

	/**
	 * Updates a single line item in the Order
	 * @param request {@link UpdateSingleLineItemRequest}
	 * @param tenantId Tenant ID
	 * @param orderId Order ID
	 * @param lineItemId Line Item ID
	 * @return {@link OrderResponse}
	 */
	OrderResponse updateSingleLineItem(UpdateSingleLineItemRequest request,
									   String tenantId,
									   String orderId,
									   String lineItemId);

	/**
	 * Adds a transaction to the order (ex: Cash, Card)
	 * @param orderId Order ID
	 * @param tenantId Tenant ID
	 * @param request {@link TransactionRequest}
	 * @return {@link ResponseEntity<OrderResponse>}
	 */
	ResponseEntity<OrderResponse> addTransaction(String orderId, String tenantId, TransactionRequest request);

	/**
	 * Deletes a specific transaction from the order
	 *
	 * @param orderId the ID of the order from which the transaction is to be deleted
	 * @param transactionId the ID of the transaction to be deleted
	 * @param tenantId Tenant ID
	 * @return {@link OrderResponse}
	 */
	OrderResponse deleteTransaction(String orderId, String transactionId, String tenantId);

	/**
	 * Adds reason codes to the order
	 * @param orderId Order ID
	 * @param tenantId Tenant ID
	 * @param request {@link UpdateOrderRequest}
	 * @return {@link OrderResponse}
	 */
	OrderResponse addReasonCodes(String orderId, String tenantId, UpdateOrderRequest request);

	/**
	 * Returns an Order with updated Card transactions applied
	 * @param orderId Order ID
	 * @param tenantId Tenant ID
	 * @return {@link OrderResponse}
	 */
	OrderResponse getOrderWithCardTransactions(String orderId, String tenantId);

	/**
	 * Returns list of Orders assigned to a specific Cash Drawer
	 * @param tenantId Tenant ID
	 * @param cashDrawerId Cash Drawer ID
	 * @return {@link OrdersByCashDrawerResponse}
	 */
	OrdersByCashDrawerResponse getOrdersByCashDrawer(String tenantId, String cashDrawerId);

	/**
	 * Returns list of Orders assigned to a specific Product
	 * @param request {@link ItemsInfoRequest}
	 * @param tenantId Tenant ID
	 * @return {@link ItemsInfoResponse}
	 */
	ItemsInfoResponse getItemsInfo(ItemsInfoRequest request, String tenantId);

	ChargeCodeListResponse getChargeCodes(String orderId, String tenantId);
}
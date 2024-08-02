package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.order.dto.requests.*;
import com.amazonaws.saas.eks.order.dto.requests.itemsinfo.ItemsInfoRequest;
import com.amazonaws.saas.eks.order.dto.responses.*;
import com.amazonaws.saas.eks.order.dto.responses.itemsinfo.ItemsInfoResponse;
import com.amazonaws.saas.eks.order.model.Permission;
import com.amazonaws.saas.eks.service.OrderService;
import com.github.fge.jsonpatch.JsonPatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class OrderController {
	private static final Logger logger = LogManager.getLogger(OrderController.class);

	@Autowired
	private OrderService orderService;

	@Autowired
	private JwtAuthManager jwtAuthManager;

	@GetMapping(value = "{tenantId}/orders", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ListOrdersResponse search(@Valid OrderSearchRequest request) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return orderService.search(request, tenantId);
		} catch (Exception e) {
			logger.error("error searching orders", e);
			throw e;
		}
	}

	@GetMapping(value = "{tenantId}/orders/{orderId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OrderResponse get(@PathVariable("orderId") String orderId) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return orderService.get(orderId, tenantId);
		} catch (Exception e) {
			logger.error(String.format("Order not found with ID: %s. TenantId: %s", orderId, tenantId), e);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/orders", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OrderResponse create(@RequestBody @Valid CreateOrderRequest request) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		String username = jwtAuthManager.getTenantUser().getUsername();
		try {
			return orderService.create(request, tenantId, username);
		} catch (Exception e) {
			logger.error(String.format("Error creating order. TenantId: %s", tenantId), e);
			throw e;
		}
	}

	@PutMapping(value = "{tenantId}/orders/{orderId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OrderResponse update(@PathVariable("orderId") String orderId,
								@RequestBody @Valid UpdateOrderRequest request) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return orderService.update(orderId, tenantId, request);
		} catch (Exception e) {
			logger.error(String.format("Error updating order. TenantId: %s", tenantId), e);
			throw e;
		}
	}

	@DeleteMapping(value = "{tenantId}/orders/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public void delete(@PathVariable String orderId) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			orderService.delete(orderId, tenantId);
		} catch (Exception e) {
			logger.error(String.format("Error deleting order. TenantId: %s", tenantId), e);
			throw e;
		}
	}

	@PatchMapping(value = "{tenantId}/orders/{orderId}", consumes = "application/json-patch+json", produces = {MediaType.APPLICATION_JSON_VALUE})
	public OrderResponse patch(@PathVariable String orderId, @RequestBody JsonPatch patch) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return orderService.patch(orderId, patch, tenantId);
		} catch (Exception e) {
			logger.error("Error patching order {}. TenantId: {}", orderId, tenantId, e);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/orders/{orderId}/import", produces = {MediaType.APPLICATION_JSON_VALUE})
	public OrderResponse importLineItems(@PathVariable String orderId, @RequestBody ImportLineItemsRequest request) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return orderService.importLineItems(orderId, tenantId, request);
		} catch (Exception e) {
			logger.error("Error importing line items to order {}. TenantId: {}", orderId, tenantId, e);
			throw e;
		}
	}

	@PutMapping(value = "{tenantId}/orders/{orderId}/line-items", produces = {MediaType.APPLICATION_JSON_VALUE})
	public OrderResponse updateLineItems(@PathVariable String orderId,
										 @RequestBody UpdateLineItemsRequest request) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return orderService.updateLineItems(request, tenantId, orderId);
		} catch (Exception e) {
			logger.error(String.format("Error updating order products. TenantId: %s", tenantId), e);
			throw e;
		}
	}

	@PutMapping(value ="{tenantId}/orders/{orderId}/line-items/{lineItemId}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public OrderResponse updateSingleLineItem(@PathVariable String orderId,
											  @PathVariable String lineItemId,
											  @RequestBody UpdateSingleLineItemRequest request) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return orderService.updateSingleLineItem(request, tenantId, orderId, lineItemId);
		} catch (Exception e) {
			logger.error(String.format("Error updating single line item. TenantId: %s", tenantId), e);
			throw e;
		}
	}

	@PutMapping(value = "{tenantId}/orders/{orderId}/transactions", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<OrderResponse> addTransaction(@PathVariable String orderId,
														@RequestBody @Valid TransactionRequest request) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return orderService.addTransaction(orderId, tenantId, request);
		} catch (Exception e) {
			logger.error(String.format("Error adding transaction to order. TenantId: %s", tenantId), e);
			throw e;
		}
	}

	@DeleteMapping(value = "{tenantId}/orders/{orderId}/transactions/{transactionId}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public OrderResponse deleteTransaction(@PathVariable String orderId, @PathVariable String transactionId) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return orderService.deleteTransaction(orderId, transactionId, tenantId);
		} catch (Exception e) {
			logger.error(String.format("Error deleting transaction from order. TenantId: %s", tenantId), e);
			throw e;
		}
	}

	@PutMapping(value = "{tenantId}/orders/{orderId}/reason-codes", produces = {MediaType.APPLICATION_JSON_VALUE})
	public OrderResponse addReasonCodes(@PathVariable String orderId,
														@RequestBody @Valid UpdateOrderRequest request) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return orderService.addReasonCodes(orderId, tenantId, request);
		} catch (Exception e) {
			logger.error(String.format("Error adding reason codes to order. TenantId: %s", tenantId), e);
			throw e;
		}
	}

	@GetMapping(value = "{tenantId}/orders/{orderId}/transactions", produces = {MediaType.APPLICATION_JSON_VALUE})
	public OrderResponse getOrderTransactions(@PathVariable String orderId) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return orderService.getOrderWithCardTransactions(orderId, tenantId);
		} catch (Exception e) {
			logger.error(String.format("Error fetching order with card transactions. TenantId: %s", tenantId), e);
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('" + Permission.SERVER_ORDER_READ + "')")
	@GetMapping(value = "{tenantId}/orders/cashdrawers/{cashDrawerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OrdersByCashDrawerResponse getAllByCashDrawer(@PathVariable String tenantId,
														 @PathVariable String cashDrawerId) {
		try {
			return orderService.getOrdersByCashDrawer(tenantId, cashDrawerId);
		} catch (Exception e) {
			logger.error(String.format("Error listing orders by cash drawer. TenantId: %s", tenantId), e);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/orders/{orderId}/save-type", produces = {MediaType.APPLICATION_JSON_VALUE})
	public OrderResponse saveType(@PathVariable String orderId, @RequestBody @Valid UpdateOrderTypeRequest request) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return orderService.saveType(request, orderId, tenantId);
		} catch (Exception e) {
			logger.error("Error saving order type. TenantId: {}", tenantId, e);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/orders/{orderId}/finalize", produces = {MediaType.APPLICATION_JSON_VALUE})
	public OrderResponse finalize(@PathVariable String orderId) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return orderService.finalize(orderId, tenantId);
		} catch (Exception e) {
			logger.error("error finalizing order. OrderId {}. TenantId {}", orderId, tenantId);
			throw e;
		}
	}

	@GetMapping(value = "{tenantId}/orders/{orderId}/charge-codes", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ChargeCodeListResponse getChargeCodes(@PathVariable String orderId) {
		String tenantId = jwtAuthManager.getTenantUser().getTenantId();
		try {
			return orderService.getChargeCodes(orderId, tenantId);
		} catch (Exception e) {
			logger.error("Error getting order's charge codes. OrderId {}. TenantId {}", orderId, tenantId);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/orders/items-info", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ItemsInfoResponse getItemsInfo(@PathVariable String tenantId,
												   @RequestBody @Valid ItemsInfoRequest request
	) {
		try {
			return orderService.getItemsInfo(request, tenantId);
		} catch (Exception e) {
			logger.error(String.format("Error getting items info. TenantId: %s", tenantId), e);
			throw e;
		}
	}

	/**
	 * Heartbeat method to check if order service is up and running
	 *
	 */
	@RequestMapping("{tenantId}/orders/health")
	public String health() {
		return "\"Order service is up!\"";
	}
}

package com.amazonaws.saas.eks.controller;

import java.util.List;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.dto.requests.orders.CreateOrderRequest;
import com.amazonaws.saas.eks.dto.requests.orders.UpdateLineItemsRequest;
import com.amazonaws.saas.eks.dto.requests.orders.UpdateOrderRequest;
import com.amazonaws.saas.eks.dto.responses.orders.OrderResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.amazonaws.saas.eks.model.Order;
import com.amazonaws.saas.eks.service.OrderService;

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
	public List<Order> getAll() {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return orderService.getOrders(tu.getTenantId());
		} catch (Exception e) {
			logger.error("Error listing orders", e);
			throw e;
		}
	}

	@GetMapping(value = "{tenantId}/orders/{orderId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OrderResponse get(@PathVariable("orderId") String orderId) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return orderService.get(orderId, tu.getTenantId());
		} catch (Exception e) {
			logger.error(String.format("Order not found with ID: %s", orderId), e);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/orders", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OrderResponse create(@RequestBody @Valid CreateOrderRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return orderService.create(request, tu.getTenantId());
		} catch (Exception e) {
			logger.error("Error creating order", e);
			throw e;
		}
	}

	@PutMapping(value = "{tenantId}/orders/{orderId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OrderResponse update(@PathVariable("orderId") String orderId,
								@RequestBody @Valid UpdateOrderRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return orderService.update(orderId, tu.getTenantId(), request);
		} catch (Exception e) {
			logger.error("Error updating order", e);
			throw e;
		}
	}

	@DeleteMapping(value = "{tenantId}/orders/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public void delete(@PathVariable String orderId) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			orderService.delete(orderId, tu.getTenantId());
		} catch (Exception e) {
			logger.error("Error deleting order", e);
			throw e;
		}
	}

	@PutMapping(value = "{tenantId}/orders/{orderId}/line-items", produces = {MediaType.APPLICATION_JSON_VALUE})
	public OrderResponse updateLineItems(@PathVariable String orderId,
										 @RequestBody UpdateLineItemsRequest request) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return orderService.updateLineItems(request, tu.getTenantId(), orderId);
		} catch (Exception e) {
			logger.error("Error updating order products", e);
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

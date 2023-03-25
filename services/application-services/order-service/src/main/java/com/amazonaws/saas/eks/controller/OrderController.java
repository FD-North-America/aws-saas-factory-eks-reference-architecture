package com.amazonaws.saas.eks.controller;

import java.util.List;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.saas.eks.model.Order;
import com.amazonaws.saas.eks.service.OrderService;

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
	public Order get(@PathVariable("orderId") String orderId) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return orderService.getOrderById(orderId, tu.getTenantId());
		} catch (Exception e) {
			logger.error(String.format("Order not found with ID: ", orderId), e);
			throw e;
		}
	}

	@PostMapping(value = "{tenantId}/orders", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Order update(@RequestBody Order order) {
		try {
			TenantUser tu = jwtAuthManager.getTenantUser();
			return orderService.save(order, tu.getTenantId());
		} catch (Exception e) {
			logger.error("Error updating order", e);
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

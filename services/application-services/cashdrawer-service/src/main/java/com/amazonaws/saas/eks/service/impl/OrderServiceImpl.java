/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.amazonaws.saas.eks.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.amazonaws.saas.eks.clients.product.ProductServiceClient;
import com.amazonaws.saas.eks.clients.product.dto.requests.PricingRequestParams;
import com.amazonaws.saas.eks.clients.product.dto.requests.ProductPricingRequest;
import com.amazonaws.saas.eks.clients.product.dto.responses.PricingResponse;
import com.amazonaws.saas.eks.clients.product.dto.responses.ProductPricingResponse;
import com.amazonaws.saas.eks.dto.requests.orders.LineItemRequest;
import com.amazonaws.saas.eks.dto.requests.orders.CreateOrderRequest;
import com.amazonaws.saas.eks.dto.requests.orders.UpdateLineItemsRequest;
import com.amazonaws.saas.eks.dto.requests.orders.UpdateOrderRequest;
import com.amazonaws.saas.eks.dto.responses.orders.LineItemResponse;
import com.amazonaws.saas.eks.dto.responses.orders.OrderResponse;
import com.amazonaws.saas.eks.exception.InvalidOrderArgumentsException;
import com.amazonaws.saas.eks.exception.OrderNotFoundException;
import com.amazonaws.saas.eks.exception.OrderUpdateStatusInvalidException;
import com.amazonaws.saas.eks.mapper.OrderMapper;
import com.amazonaws.saas.eks.model.CashDrawer;
import com.amazonaws.saas.eks.model.Discount;
import com.amazonaws.saas.eks.model.LineItem;
import com.amazonaws.saas.eks.model.enums.LineItemType;
import com.amazonaws.saas.eks.model.enums.OrderStatus;
import com.amazonaws.saas.eks.repository.CashDrawerRepository;
import com.amazonaws.saas.eks.repository.DiscountRepository;
import com.amazonaws.saas.eks.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amazonaws.saas.eks.model.Order;
import com.amazonaws.saas.eks.repository.OrderRepository;
import org.springframework.util.StringUtils;

@Service
public class OrderServiceImpl implements OrderService {

	private static final String DISCOUNT_NAME = "DISCOUNT";
	private static final String TAXABLE = "Taxable";

	private OrderRepository orderRepository;

	private DiscountRepository discountRepository;

	private CashDrawerRepository cashDrawerRepository;

	@Autowired
	private ProductServiceClient productServiceClient;

	public OrderServiceImpl(OrderRepository orderRepository,
							DiscountRepository discountRepository,
							CashDrawerRepository cashDrawerRepository) {
		this.orderRepository = orderRepository;
		this.discountRepository = discountRepository;
		this.cashDrawerRepository = cashDrawerRepository;
	}

	@Override
	public List<Order> getOrders(String tenantId) {
		return orderRepository.getOrders(tenantId);
	}

	@Override
	public OrderResponse get(String orderId, String tenantId) {
		Order order = orderRepository.getOrderById(orderId, tenantId);
		if (order == null) {
			throw new OrderNotFoundException(orderId);
		}
		return buildOrderResponse(order);
	}

	@Override
	public OrderResponse create(CreateOrderRequest request, String tenantId) {
		Order order = new Order();
		order.setStatus(OrderStatus.PENDING.toString());

		if (!request.getLineItems().isEmpty()) {
			populateOrderLineItemDetails(request.getLineItems(), tenantId, order);
		}
		if (StringUtils.hasLength(request.getCashDrawerId())) {
			CashDrawer cashDrawer = cashDrawerRepository.get(request.getCashDrawerId(), tenantId);
			if (cashDrawer == null) {
				throw new InvalidOrderArgumentsException("Cash Drawer is null");
			}
			order.setCashDrawerId(request.getCashDrawerId());
		}
		Order model = orderRepository.save(order, tenantId);

		return buildOrderResponse(model);
	}

	@Override
	public OrderResponse updateLineItems(UpdateLineItemsRequest request, String tenantId, String orderId) {
		Order order = orderRepository.getOrderById(orderId, tenantId);
		if (order == null) {
			throw new OrderNotFoundException(orderId);
		}

		if (!order.getStatus().equals(OrderStatus.PENDING.toString())) {
			throw new OrderUpdateStatusInvalidException(orderId);
		}

		if (request.getLineItems().isEmpty()) {
			order.setLineItems(new ArrayList<>());
			clearTotals(order);
		} else {
			populateOrderLineItemDetails(request.getLineItems(), tenantId, order);
		}
		Order model = orderRepository.save(order, tenantId);

		return buildOrderResponse(model);
	}

	private void populateOrderLineItemDetails(List<LineItemRequest> lineItems, String tenantId, Order order) {
		if (lineItems.stream().anyMatch(l -> l.getType() == null)) {
			throw new InvalidOrderArgumentsException("LineItem type cannot be null");
		}

		List<LineItem> productLineItems = new ArrayList<>();
		BigDecimal taxRate = BigDecimal.ZERO;

		// Get Pricing Details
		List<LineItemRequest> products = filterLineItems(lineItems, LineItemType.PRODUCT);
		if (!products.isEmpty()) {
			PricingResponse pricing = getPricingDetails(tenantId, products);
			productLineItems = buildProductLineItems(order, pricing);
			taxRate = pricing.getTaxRate();
		}

		// Build Discount Line Items
		List<LineItemRequest> discountRequests = filterLineItems(lineItems, LineItemType.DISCOUNT);
		List<LineItem> discountLineItems = buildDiscountLineItems(order, discountRequests, tenantId);

		// Add Line Items to Order
		List<LineItem> newLineItems = new ArrayList<>();
		newLineItems.addAll(productLineItems);
		newLineItems.addAll(discountLineItems);
		newLineItems.sort(Comparator.comparing(LineItem::getCreated));
		order.setLineItems(newLineItems);

		// Calculate pricing totals
		calculateTotals(order, taxRate);
	}

	private static OrderResponse buildOrderResponse(Order model) {
		OrderResponse response = OrderMapper.INSTANCE.orderToOrderResponse(model);
		for (LineItem l : model.getLineItems()) {
			LineItemResponse itemResponse = OrderMapper.INSTANCE.lineItemToLineItemResponse(l);
			if (itemResponse.getType().equals(LineItemType.DISCOUNT.toString())) {
				BigDecimal price = itemResponse.getPrice().negate();
				itemResponse.setPrice(price);
				itemResponse.setExtendedPrice(price);
			}
		}
		return response;
	}

	@Override
	public OrderResponse update(String orderId, String tenantId, UpdateOrderRequest request) {
		Order order = OrderMapper.INSTANCE.updateOrderRequestToOrder(request);
		order.setId(orderId);
		Order updatedOrder = orderRepository.update(order, tenantId);
		return OrderMapper.INSTANCE.orderToOrderResponse(updatedOrder);
	}

	@Override
	public void delete(String orderId, String tenantId) {
		orderRepository.delete(orderId, tenantId);
	}

	private PricingResponse getPricingDetails(String tenantId, List<LineItemRequest> orderProducts) {
		PricingRequestParams request = new PricingRequestParams();
		for (LineItemRequest o : orderProducts) {
			ProductPricingRequest r = new ProductPricingRequest();
			r.setProductId(o.getId());
			r.setQuantity(o.getQuantity());
			r.setUomId(o.getUomId());
			r.setBarcode(o.getBarcode());
			request.getProductPricingRequests().add(r);
		}
		ResponseEntity<PricingResponse> response = productServiceClient.getPricingDetails(tenantId, request);
		if (response.getStatusCode() != HttpStatus.OK) {
			throw new RuntimeException("Error getting response from Product Service");
		}
		return response.getBody();
	}

	private Map<String, LineItem> buildLineItemMap(List<LineItem> lineItems, LineItemType type) {
		return lineItems.stream()
				.filter(l -> l.getType().equals(type.toString()))
				.collect(Collectors.toMap(LineItem::getId, l -> l));
	}

	private List<LineItemRequest> filterLineItems(List<LineItemRequest> lineItems, LineItemType lineItemType) {
		return lineItems
				.stream()
				.filter(l -> l.getType().equals(lineItemType.toString()))
				.collect(Collectors.toList());
	}

	private List<LineItem> buildProductLineItems(Order order, PricingResponse pricing) {
		List<LineItem> productLineItems = new ArrayList<>();
		Map<String, LineItem> productIdMap = buildLineItemMap(order.getLineItems(), LineItemType.PRODUCT);
		for (String productId : pricing.getProductPricing().keySet()) {
			ProductPricingResponse p = pricing.getProductPricing().get(productId);
			LineItem l = OrderMapper.INSTANCE.productPricingResponseToLineItem(p);
			l.setType(LineItemType.PRODUCT.toString());

			if (p.getVolumePricing() != null && p.getQuantity() > p.getVolumePricing().getBreakPointQty()) {
				l.setPrice(roundValue(p.getVolumePricing().getPrice()));
			} else {
				l.setPrice(roundValue(p.getRetailPrice()));
			}

			if (productIdMap.containsKey(productId)) {
				l.setCreated(productIdMap.get(productId).getCreated());
			} else {
				l.setCreated(new Date());
			}

			l.setExtendedPrice(roundValue(p.getRetailPrice().multiply(BigDecimal.valueOf(l.getQuantity()))));
			productLineItems.add(l);
		}

		return productLineItems;
	}

	private List<LineItem> buildDiscountLineItems(Order order, List<LineItemRequest> discountRequests, String tenantId) {
		List<LineItem> discountList = new ArrayList<>();
		Map<String, LineItem> discountIdMap = buildLineItemMap(order.getLineItems(), LineItemType.DISCOUNT);
		for (LineItemRequest r : discountRequests) {
			Discount discount;
			if (!StringUtils.hasLength(r.getId()) && discountIdMap.containsKey(r.getId())) {
				discount = discountRepository.get(r.getId(), tenantId);
			} else {
				discount = new Discount();
				discount.setOrderId(order.getId());
			}

			discount.setPrice(roundValue(r.getPrice()));
			Discount updatedDiscount = discountRepository.save(discount, tenantId);
			LineItem discountLineItem = OrderMapper.INSTANCE.discountToLineItem(updatedDiscount);
			discountLineItem.setType(LineItemType.DISCOUNT.toString());
			discountLineItem.setExtendedPrice(discountLineItem.getPrice());
			discountLineItem.setName(DISCOUNT_NAME);
			discountLineItem.setQuantity(1);

			discountList.add(discountLineItem);
		}

		return discountList;
	}

	private void calculateTotals(Order order, BigDecimal taxRate) {
		BigDecimal taxableSubTotal = BigDecimal.ZERO;
		BigDecimal nonTaxableSubTotal = BigDecimal.ZERO;
		BigDecimal discountTotal = BigDecimal.ZERO;

		for (LineItem l : order.getLineItems()) {
			if (l.getType().equals(LineItemType.DISCOUNT.toString())) {
				discountTotal = discountTotal.add(l.getPrice());
			} else {
				if (l.getTaxable().equals(TAXABLE)) {
					taxableSubTotal = taxableSubTotal.add(l.getExtendedPrice());
				} else {
					nonTaxableSubTotal = nonTaxableSubTotal.add(l.getExtendedPrice());
				}
			}
		}

		order.setTaxableSubTotal(roundValue(taxableSubTotal));
		order.setNonTaxableSubTotal(roundValue(nonTaxableSubTotal));
		order.setSubTotal(roundValue(taxableSubTotal.add(nonTaxableSubTotal)));
		order.setDiscountTotal(roundValue(discountTotal));

		BigDecimal taxTotal;
		if (taxableSubTotal.compareTo(discountTotal) > 0) {
			taxTotal = taxableSubTotal.subtract(discountTotal).multiply(taxRate);
		} else {
			taxTotal = taxableSubTotal.multiply(taxRate);
		}
		order.setTaxTotal(roundValue(taxTotal));
		BigDecimal total = order.getSubTotal().subtract(order.getDiscountTotal()).add(order.getTaxTotal());
		order.setTaxTotal(roundValue(taxTotal));
		order.setTotal(roundValue(total));
	}

	private void clearTotals(Order order) {
		order.setTotal(BigDecimal.ZERO);
		order.setTaxTotal(BigDecimal.ZERO);
		order.setSubTotal(BigDecimal.ZERO);
		order.setTaxableSubTotal(BigDecimal.ZERO);
		order.setNonTaxableSubTotal(BigDecimal.ZERO);
		order.setDiscountTotal(BigDecimal.ZERO);
	}

	private BigDecimal roundValue(BigDecimal value) {
		return value.setScale(2, RoundingMode.UP);
	}
}
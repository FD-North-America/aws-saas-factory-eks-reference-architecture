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
package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.OrderException;
import com.amazonaws.saas.eks.model.Order;
import com.amazonaws.saas.eks.model.Transaction;
import com.amazonaws.saas.eks.model.enums.EntityType;
import com.amazonaws.saas.eks.model.enums.OrderStatus;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Repository
public class OrderRepository extends BaseRepository {
	private static final Logger logger = LogManager.getLogger(OrderRepository.class);

	/**
	 * Method to get all orders for a tenant
	 * @param tenantId
	 * @return List<Order>
	 */
	public List<Order> getOrders(String tenantId) {
		PaginatedScanList<Order> results = null;
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);

		try {
			results = mapper.scan(Order.class, new DynamoDBScanExpression());
		} catch (Exception e) {
			String message = String.format("TenantId: %s-Get Orders failed %s", tenantId, e.getMessage());
			logger.error(message);
			throw new OrderException(message);
		}

		return results;
	}

	/**
	 * Method to save an order for a tenant
	 * @param order
	 * @param tenantId
	 * @return Order
	 */
	public Order save(Order order, String tenantId) {
		try {
			DynamoDBMapper mapper = dynamoDBMapper(tenantId);
			if (!StringUtils.hasLength(order.getPartitionKey())) {
				order.setPartitionKey(EntityType.ORDERS.getLabel());
			}
			if (!StringUtils.hasLength(order.getId())) {
				order.setId(String.valueOf(UUID.randomUUID()));
			}
			if (order.getCreated() == null) {
				order.setCreated(new Date());
			}
			if (order.getModified() == null) {
				order.setModified(order.getCreated());
			} else {
				order.setModified(new Date());
			}
			if (!StringUtils.hasLength(order.getNumber())) {
				order.setNumber(String.format("%s%s", tenantId.substring(0, 2).toUpperCase(), Instant.now().getEpochSecond()));
			}
			mapper.save(order);
		} catch (Exception e) {
			String message = String.format("TenantId: %s-Save Order failed %s", tenantId, e.getMessage());
			logger.error(message);
			throw new OrderException(message);
		}

		return order;
	}

	public Order update(Order order, String tenantId) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);
		Order model = getOrderById(order.getId(), tenantId);
		Date modified = new Date();

		try {
			if (StringUtils.hasLength(order.getStatus()) && !order.getStatus().equals(OrderStatus.DELETED.toString())) {
				model.setStatus(order.getStatus());
			}
			model.setTransactions(new ArrayList<>());
			if (!order.getTransactions().isEmpty()) {
				for (Transaction t : order.getTransactions()) {
					t.setDate(modified);
					model.getTransactions().add(t);
				}
			}

			model.setCreditPaymentAmount(order.getCreditPaymentAmount() == null ? BigDecimal.ZERO : order.getCreditPaymentAmount());
			model.setCashPaymentAmount(order.getCashPaymentAmount() == null ? BigDecimal.ZERO : order.getCashPaymentAmount());
			if (model.getCashPaymentAmount().compareTo(BigDecimal.ZERO) > 0 || model.getCreditPaymentAmount().compareTo(BigDecimal.ZERO) > 0) {
				model.setBalanceDue(model.getTotal().subtract(model.getCashPaymentAmount()).subtract(model.getCreditPaymentAmount()));
			}
			model.setModified(modified);
			mapper.save(model);
		} catch (Exception e) {
			String message = String.format("TenantId: %s-Update Orders failed %s", tenantId, e.getMessage());
			logger.error(message);
			throw new OrderException(message);
		}

		return model;
	}

	/**
	 * Method to get order by Id for a tenant
	 * @param orderId
	 * @param tenantId
	 * @return Order
	 */
	public Order getOrderById(String orderId, String tenantId) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);
		Order order = null;

		try {
			order = mapper.load(Order.class, EntityType.ORDERS.getLabel(), orderId);
		} catch (Exception e) {
			String message = String.format("TenantId: %s-Get Order by ID failed %s", tenantId, e.getMessage());
			logger.error(message);
			throw new OrderException(message);
		}

		return order;
	}

	/**
	 * Method to delete a tenant's order
	 * @param orderId
	 * @param tenantId
	 */
	public void delete(String orderId, String tenantId) {
		try {
			DynamoDBMapper mapper = dynamoDBMapper(tenantId);
			Order order = getOrderById(orderId, tenantId);
			order.setStatus(OrderStatus.DELETED.toString());
			order.setModified(new Date());
			mapper.save(order);
		} catch (Exception e) {
			String message = String.format("TenantId: %s-Delete Orders failed %s", tenantId, e.getMessage());
			logger.error(message);
			throw new OrderException(message);
		}
	}

	public List<Order> getOrdersByCashDrawer(String cashDrawerId, String tenantId) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);
		Map<String, AttributeValue> eav = new HashMap<>();
		Map<String, String> ean = new HashMap<>();
		ean.put("#"+ Order.STATUS, Order.STATUS); // create alias for reserved word Status
		eav.put(":partitionKey", new AttributeValue().withS(EntityType.ORDERS.getLabel()));
		eav.put(":cashDrawerId", new AttributeValue().withS(cashDrawerId));
		eav.put(":status", new AttributeValue().withS(OrderStatus.PAID.toString()));
		DynamoDBQueryExpression<Order> query = new DynamoDBQueryExpression<Order>()
				.withIndexName(Order.CASH_DRAWER_ID_INDEX)
				.withConsistentRead(false)
				.withFilterExpression(String.format("#%s = :status", Order.STATUS))
				.withKeyConditionExpression(String.format("%s = :partitionKey AND %s = :cashDrawerId", Order.PARTITION_KEY,
						Order.CASH_DRAWER_ID))
				.withExpressionAttributeValues(eav)
				.withExpressionAttributeNames(ean);
		try {
			return mapper.query(Order.class, query);
		} catch (Exception e) {
			String message = String.format("TenantId: %s-Get Orders By Cash Drawer failed %s", tenantId, e.getMessage());
			logger.error(message);
			throw new OrderException(message);
		}
	}
}

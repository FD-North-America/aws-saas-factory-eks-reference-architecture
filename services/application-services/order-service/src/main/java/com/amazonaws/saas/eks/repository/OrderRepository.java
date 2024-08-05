package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.OrderException;
import com.amazonaws.saas.eks.order.model.ChargeCode;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.order.model.ProductOrder;
import com.amazonaws.saas.eks.order.model.Transaction;
import com.amazonaws.saas.eks.order.model.enums.EntityType;
import com.amazonaws.saas.eks.order.model.enums.LineItemType;
import com.amazonaws.saas.eks.order.model.enums.OrderStatus;
import com.amazonaws.saas.eks.order.model.enums.OrderType;
import com.amazonaws.saas.eks.order.model.search.OrderSearchResponse;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldSort;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.mapping.FieldType;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Repository
public class OrderRepository extends BaseRepository {
	private static final Logger logger = LogManager.getLogger(OrderRepository.class);

	private static final String SORT_BY_NUMBER = "number";
	private static final String SORT_BY_DATE = "name";
	private static final String SORT_BY_CUSTOMER_NUMBER = "customer_number";
	private static final String SORT_BY_CUSTOMER_NAME = "customer_name";
	private static final String SORT_BY_IDENTIFIER = "identifier";
	private static final String SORT_BY_ACCOUNT_NAME = "account_name";
	private static final String SORT_BY_PO_NUMBER = "po_number";
	private static final String SORT_BY_AMOUNT_DUE = "amount_due";
	private static final String SORT_BY_TENDERED = "tendered";
	private static final String SORT_BY_EXPIRATION_DATE = "exp_date";
	private static final String SORT_BY_DESC = "desc";

	@Autowired
	private OpenSearchClient openSearchClient;

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
		Date now = new Date();
		try {
			if (StringUtils.hasLength(order.getStatus())) {
				model.setStatus(order.getStatus());
			}
			if (order.getCashPaymentAmount() != null) {
				model.setCashPaymentAmount(order.getCashPaymentAmount());
			}
			if (order.getCreditPaymentAmount() != null) {
				model.setCreditPaymentAmount(order.getCreditPaymentAmount());
			}
			if (order.getTransactions() != null) {
				model.setTransactions(order.getTransactions());
				for (Transaction t: model.getTransactions()) {
					t.setDate(now);
				}

				if (order.getBalanceDue() != null && order.getBalanceDue().compareTo(BigDecimal.ZERO) < 0) {
					model.setPaidDate(now);
				}
			}
			if (order.getPaidDate() != null) {
				model.setPaidDate(order.getPaidDate());
			}
			if (StringUtils.hasLength(order.getType())) {
				model.setType(order.getType());
			}
			model.setBalanceDue(order.getBalanceDue());
			model.setTotalCost(order.getTotalCost());
			model.setProfit(order.getProfit());
			model.setMargin(order.getMargin());
			model.setPaidOutTotal(order.getPaidOutTotal());
			model.setCardTransactionCount(order.getCardTransactionCount());
			model.setCashTransactionCount(order.getCashTransactionCount());
			model.setCustomerId(order.getCustomerId());
			model.setCustomerName(order.getCustomerName());
			model.setCustomerNumber(order.getCustomerNumber());
			model.setAccountName(order.getAccountName());
			model.setAccountNumber(order.getAccountNumber());
			model.setIdentifier(order.getIdentifier());
			model.setBuyer(order.getBuyer());
			model.setPoNumber(order.getPoNumber());
			model.setCreditLimit(order.getCreditLimit());
			model.setBalance(order.getBalance());
			model.setLinkedOrderId(order.getLinkedOrderId());
			model.setModified(now);
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
		try {
			return mapper.load(Order.class, EntityType.ORDERS.getLabel(), orderId);
		} catch (Exception e) {
			String message = String.format("TenantId: %s-Get Order by ID failed %s", tenantId, e.getMessage());
			logger.error(message);
			throw new OrderException(message);
		}
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
			if (order.getType().equals(OrderType.INVOICE.toString())) {
				throw new OrderException("invoices can not be deleted");
			}
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
		ean.put("#"+ Order.DbAttrNames.STATUS, Order.DbAttrNames.STATUS); // create alias for reserved word Status
		eav.put(":partitionKey", new AttributeValue().withS(EntityType.ORDERS.getLabel()));
		eav.put(":cashDrawerId", new AttributeValue().withS(cashDrawerId));
		eav.put(":status", new AttributeValue().withS(OrderStatus.PAID.toString()));
		DynamoDBQueryExpression<Order> query = new DynamoDBQueryExpression<Order>()
				.withIndexName(Order.DbIndexNames.CASH_DRAWER_ID_INDEX)
				.withConsistentRead(false)
				.withFilterExpression(String.format("#%s = :status", Order.DbAttrNames.STATUS))
				.withKeyConditionExpression(String.format("%s = :partitionKey AND %s = :cashDrawerId",
						Order.DbAttrNames.PARTITION_KEY, Order.DbAttrNames.CASH_DRAWER_ID))
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

	public List<ProductOrder> getOrdersByProduct(String productId, String tenantId) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);
		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(":partitionKey", new AttributeValue().withS(EntityType.PRODUCT_ORDERS.getLabel()));
		eav.put(":productId", new AttributeValue().withS(productId));

		DynamoDBQueryExpression<ProductOrder> query = new DynamoDBQueryExpression<ProductOrder>()
				.withKeyConditionExpression(String.format("%s = :partitionKey and begins_with(%s, :productId)", ProductOrder.DbAttrNames.PARTITION_KEY, ProductOrder.DbAttrNames.SORT_KEY))
				.withExpressionAttributeValues(eav);

		try {
			return mapper.query(ProductOrder.class, query);
		} catch (Exception e) {
			String message = String.format("TenantId: %s-Get Orders By Product failed %s", tenantId, e.getMessage());
			logger.error(message);
			throw new OrderException(message);
		}
	}

	public OrderSearchResponse search(String tenantId,
									  int from,
									  int size,
									  String filter,
									  OrderType orderType,
									  String sortBy) {
		List<Query> filterQueries = new ArrayList<>();

		TermQuery typeQuery = new TermQuery.Builder()
				.field(Order.OpenSearch.FieldNames.TYPE_KEYWORD)
				.value(FieldValue.of(orderType.toString()))
				.build();
		filterQueries.add(typeQuery._toQuery());

		List<Query> matchQueries = new ArrayList<>();
		int minShouldMatch = 0;
		if (StringUtils.hasLength(filter)) {
			MatchBoolPrefixQuery numberQuery = new MatchBoolPrefixQuery.Builder()
					.field(Order.OpenSearch.FieldNames.NUMBER)
					.query(filter)
					.build();
			matchQueries.add(numberQuery._toQuery());

			MatchBoolPrefixQuery customerNumberQuery = new MatchBoolPrefixQuery.Builder()
					.field(Order.OpenSearch.FieldNames.CUSTOMER_NUMBER)
					.query(filter)
					.build();
			matchQueries.add(customerNumberQuery._toQuery());

			MatchBoolPrefixQuery customerNameQuery = new MatchBoolPrefixQuery.Builder()
					.field(Order.OpenSearch.FieldNames.CUSTOMER_NAME)
					.query(filter)
					.build();
			matchQueries.add(customerNameQuery._toQuery());

			MatchBoolPrefixQuery identifierQuery = new MatchBoolPrefixQuery.Builder()
					.field(Order.OpenSearch.FieldNames.IDENTIFIER)
					.query(filter)
					.build();
			matchQueries.add(identifierQuery._toQuery());

			MatchBoolPrefixQuery accountQuery = new MatchBoolPrefixQuery.Builder()
					.field(Order.OpenSearch.FieldNames.ACCOUNT_NAME)
					.query(filter)
					.build();
			matchQueries.add(accountQuery._toQuery());

			MatchBoolPrefixQuery poNumberQuery = new MatchBoolPrefixQuery.Builder()
					.field(Order.OpenSearch.FieldNames.PO_NUMBER)
					.query(filter)
					.build();
			matchQueries.add(poNumberQuery._toQuery());
			minShouldMatch = 1;
		}

		List<Query> mustNotQueries = new ArrayList<>();
		MatchQuery statusQuery = new MatchQuery.Builder()
				.field(Order.OpenSearch.FieldNames.STATUS)
				.query(FieldValue.of(OrderStatus.DELETED.toString()))
				.build();
		mustNotQueries.add(statusQuery._toQuery());

		List<SortOptions> sorting = new ArrayList<>();
		if (StringUtils.hasLength(sortBy)) {
			String field = getSearchSortField(sortBy);
			if (StringUtils.hasLength(field)) {
				SortOrder order = sortBy.contains(SORT_BY_DESC) ? SortOrder.Desc : SortOrder.Asc;
				FieldSort fieldSort = FieldSort.of(f -> f.field(field).unmappedType(FieldType.Keyword).order(order));
				sorting.add(SortOptions.of(s -> s.field(fieldSort)));
			}
		}

		BoolQuery boolQuery = new BoolQuery.Builder()
				.should(matchQueries)
				.filter(filterQueries)
				.mustNot(mustNotQueries)
				.minimumShouldMatch(String.valueOf(minShouldMatch))
				.build();

		SearchRequest req = SearchRequest.of(s -> s
				.index(Order.OpenSearch.getIndex(tenantId))
				.query(boolQuery._toQuery())
				.sort(sorting)
				.from(from)
				.size(size));

		SearchResponse<JsonNode> results;
		try {
			results = openSearchClient.search(req, JsonNode.class);
		} catch (Exception ex) {
			String message = String.format("error fetching order data from openSearch. TenantId %s", tenantId);
			logger.error(message, ex);
			throw new OrderException(message);
		}

		OrderSearchResponse response = new OrderSearchResponse();
		response.setOrders(convertSearchResultsToModels(results, Order.class, tenantId));
		for (Order o : response.getOrders()) {
			o.getLineItems().removeIf(i -> i.getType().equals(LineItemType.DISCOUNT.toString()));
		}
		response.setCount(results.hits().total().value());
		return response;
	}

	private String getSearchSortField(String sortBy) {
		if (sortBy.toLowerCase().contains(SORT_BY_CUSTOMER_NAME)) {
			return Order.OpenSearch.FieldNames.CUSTOMER_NAME_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_CUSTOMER_NUMBER)) {
			return Order.OpenSearch.FieldNames.CUSTOMER_NUMBER_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_IDENTIFIER)) {
			return Order.OpenSearch.FieldNames.IDENTIFIER_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_ACCOUNT_NAME)) {
			return Order.OpenSearch.FieldNames.ACCOUNT_NAME_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_PO_NUMBER)) {
			return Order.OpenSearch.FieldNames.PO_NUMBER_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_AMOUNT_DUE)) {
			return Order.OpenSearch.FieldNames.BALANCE_DUE_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_NUMBER)) {
			return Order.OpenSearch.FieldNames.NUMBER_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_EXPIRATION_DATE)) {
			return Order.OpenSearch.FieldNames.EXPIRATION_DATE_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_TENDERED)) {
			return Order.OpenSearch.FieldNames.TENDERED_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_DATE)) {
			return Order.OpenSearch.FieldNames.CREATED_KEYWORD;
		}

		return "";
	}

	public List<ChargeCode> getChargeCodes(String orderId, String tenantId) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);
		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(":partitionKey", new AttributeValue().withS(EntityType.CHARGE_CODES.getLabel()));
		eav.put(":orderId", new AttributeValue().withS(orderId));

		DynamoDBQueryExpression<ChargeCode> query = new DynamoDBQueryExpression<ChargeCode>()
				.withIndexName(ChargeCode.DbIndexNames.ORDER_ID_INDEX)
				.withConsistentRead(false)
				.withKeyConditionExpression(String.format("%s = :partitionKey AND %s = :orderId",
						ChargeCode.DbAttrNames.PARTITION_KEY, ChargeCode.DbAttrNames.ORDER_ID))
				.withExpressionAttributeValues(eav);
		try {
			return mapper.query(ChargeCode.class, query);
		} catch (Exception e) {
			String message = String.format("TenantId: %s-Get charge codes by orderId failed %s", tenantId, e.getMessage());
			logger.error(message);
			throw new OrderException(message);
		}
	}
}

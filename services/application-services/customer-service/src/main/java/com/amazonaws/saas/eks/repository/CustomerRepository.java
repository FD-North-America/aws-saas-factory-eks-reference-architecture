package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.customer.model.Customer;
import com.amazonaws.saas.eks.customer.model.enums.EntityStatus;
import com.amazonaws.saas.eks.customer.model.enums.EntityType;
import com.amazonaws.saas.eks.customer.model.search.CustomerSearchResponse;
import com.amazonaws.saas.eks.exception.CustomerException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.opensearch._types.FieldSort;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchBoolPrefixQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.TermQuery;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public class CustomerRepository extends BaseRepository {
	private static final Logger logger = LogManager.getLogger(CustomerRepository.class);

	private static final String NUMBER_PREFIX = "CUST";
	private static final String SORT_BY_NUMBER = "number";
	private static final String SORT_BY_NAME = "name";
	private static final String SORT_BY_PHONE_NUMBER = "phone_number";
	private static final String SORT_BY_CREDIT_LIMIT = "credit_limit";
	private static final String SORT_BY_BALANCE = "balance";
	private static final String SORT_BY_DESC = "desc";

	/**
	 * Method to save a Customer for a tenant
	 * @param customer {@link Customer to save}
	 * @param tenantId Tenant ID
	 * @return the saved {@link Customer model}
	 */
	public Customer create(Customer customer, String tenantId) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);
		if (!StringUtils.hasLength(customer.getPartitionKey())) {
			customer.setPartitionKey(EntityType.CUSTOMERS.getLabel());
		}
		if (!StringUtils.hasLength(customer.getId())) {
			customer.setId(String.valueOf(UUID.randomUUID()));
		}
		if (customer.getCreated() == null) {
			customer.setCreated(new Date());
		}
		if (customer.getModified() == null) {
			customer.setModified(customer.getCreated());
		}
		customer.setStatus(EntityStatus.ACTIVE.toString());
		try {
			int count = getLatestCounter(tenantId, EntityType.CUSTOMERS);
			customer.setNumber(String.format("%s%04d", NUMBER_PREFIX, count));
			mapper.save(customer);
		} catch (Exception e) {
			String message = String.format("TenantId: %s-Save Customer failed %s", tenantId, e.getMessage());
			logger.error(message);
			throw new CustomerException(message);
		}

		return customer;
	}

	/**
	 * Returns a Customer by its ID
	 * @param customerId CustomerID to look for
	 * @param tenantId TenantID
	 * @return {@link Customer object}
	 */
	public Customer getById(String customerId, String tenantId) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);
		try {
			return mapper.load(Customer.class, EntityType.CUSTOMERS.getLabel(), customerId);
		} catch (Exception e) {
			String message = String.format("TenantId: %s-Get Customer by ID failed %s", tenantId, e.getMessage());
			logger.error(message);
			throw new CustomerException(message);
		}
	}

	/**
	 * Updates the given Customer object
	 * @param customer {@link Customer} to modify
	 * @param tenantId TenantID
	 * @return updated {@link Customer} object
	 */
	public Customer update(Customer customer, String tenantId) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);
		Customer model = getById(customer.getId(), tenantId);
		model.setName(customer.getName());
		model.setEmail(customer.getEmail());
		model.setPhoneNumber(customer.getPhoneNumber());
		model.setLoyaltyNumber(customer.getLoyaltyNumber());
		model.setBillingAddress(customer.getBillingAddress());
		model.setShippingAddress(customer.getShippingAddress());
		model.setCreditLimit(customer.getCreditLimit());
		model.setBalance(customer.getBalance());
		model.setModified(new Date());
		try {
			mapper.save(model);
		} catch (Exception e) {
			String message = String.format("TenantId: %s-Update Customer failed %s", tenantId, e.getMessage());
			logger.error(message);
			throw new CustomerException(message);
		}

		return model;
	}

	/**
	 * Soft Deletes the Customer
	 * @param customerId CustomerID
	 * @param tenantId TenantID
	 */
	public void delete(String customerId, String tenantId) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);
		Customer customer = getById(customerId, tenantId);
		customer.setStatus(EntityStatus.DELETED.toString());
		customer.setModified(new Date());
		try {
			mapper.save(customer);
		} catch (Exception e) {
			String message = String.format("TenantId: %s-Delete Customer failed %s", tenantId, e.getMessage());
			logger.error(message);
			throw new CustomerException(message);
		}
	}

	public CustomerSearchResponse search(String tenantId,
										 int from,
										 int size,
										 String filter,
										 String sortBy) {

		// Only returning Active Customers
		TermQuery statusQuery = new TermQuery.Builder()
				.field(Customer.OpenSearch.FieldNames.STATUS_KEYWORD)
				.value(FieldValue.of(EntityStatus.ACTIVE.toString()))
				.build();
		List<Query> filterQueries = new ArrayList<>();
		filterQueries.add(statusQuery._toQuery());

		// Add any search filters
		List<Query> matchQueries = new ArrayList<>();
		int minShouldMatch = 0;
		if (StringUtils.hasLength(filter)) {
			MatchBoolPrefixQuery nameQuery = new MatchBoolPrefixQuery.Builder()
					.field(Customer.OpenSearch.FieldNames.NAME)
					.query(filter)
					.build();
			matchQueries.add(nameQuery._toQuery());

			MatchBoolPrefixQuery numberQuery = new MatchBoolPrefixQuery.Builder()
					.field(Customer.OpenSearch.FieldNames.NUMBER)
					.query(filter)
					.build();
			matchQueries.add(numberQuery._toQuery());

			MatchBoolPrefixQuery phoneNumberQuery = new MatchBoolPrefixQuery.Builder()
					.field(Customer.OpenSearch.FieldNames.PHONE_NUMBER)
					.query(filter)
					.build();
			matchQueries.add(phoneNumberQuery._toQuery());
			minShouldMatch = 1;
		}

		// Add any custom sorting
		List<SortOptions> sorting = new ArrayList<>();
		if (StringUtils.hasLength(sortBy)) {
			String field = getSearchSortField(sortBy);
			if (StringUtils.hasLength(field)) {
				SortOrder order = sortBy.contains(SORT_BY_DESC) ? SortOrder.Desc : SortOrder.Asc;
				FieldSort fieldSort = FieldSort.of(f -> f.field(field).order(order));
				sorting.add(SortOptions.of(s -> s.field(fieldSort)));
			}
		}

		BoolQuery boolQuery = new BoolQuery.Builder()
				.should(matchQueries)
				.filter(filterQueries)
				.minimumShouldMatch(String.valueOf(minShouldMatch))
				.build();

		SearchRequest req = SearchRequest.of(s -> s
				.index(Customer.OpenSearch.getIndex(tenantId))
				.query(boolQuery._toQuery())
				.sort(sorting)
				.from(from)
				.size(size));

		SearchResponse<JsonNode> results;
		try {
			results = openSearchClient.search(req, JsonNode.class);
		} catch (Exception ex) {
			String message = String.format("Error fetching customer data from openSearch. TenantId %s", tenantId);
			logger.error(message, ex);
			throw new CustomerException(message);
		}

		CustomerSearchResponse response = new CustomerSearchResponse();
		response.setCustomers(convertSearchResultsToModels(results, Customer.class, tenantId));
		response.setCount(results.hits().total().value());
		return response;
	}

	private String getSearchSortField(String sortBy) {
		if (sortBy.toLowerCase().contains(SORT_BY_NUMBER)) {
			return Customer.OpenSearch.FieldNames.NUMBER_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_NAME)) {
			return Customer.OpenSearch.FieldNames.NAME_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_PHONE_NUMBER)) {
			return Customer.OpenSearch.FieldNames.PHONE_NUMBER_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_CREDIT_LIMIT)) {
			return Customer.OpenSearch.FieldNames.CREDIT_LIMIT_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_BALANCE)) {
			return Customer.OpenSearch.FieldNames.BALANCE_KEYWORD;
		}

		return "";
	}
}

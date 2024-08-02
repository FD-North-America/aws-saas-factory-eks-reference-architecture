package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.ProductExistsException;
import com.amazonaws.saas.eks.exception.ProductNotFoundException;
import com.amazonaws.saas.eks.exception.ProductSearchException;
import com.amazonaws.saas.eks.exception.SearchException;
import com.amazonaws.saas.eks.product.model.Product;
import com.amazonaws.saas.eks.product.model.ProductSearchResponse;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.opensearch._types.FieldSort;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Repository
public class ProductRepository extends BaseRepository {
	private static final Logger logger = LogManager.getLogger(ProductRepository.class);
	private static final String PARTITION_KEY_PLACEHOLDER = ":partitionKey";
	private static final String VENDOR_ID_PLACEHOLDER = ":vendorId";
	private static final String SKU_PLACEHOLDER = ":sku";

	private static final String SORT_BY_NAME = "name";
	private static final String SORT_BY_SKU = "sku";
	private static final String SORT_BY_QUANTITY_ON_HAND = "qty_on_hand";
	private static final String SORT_BY_RETAIL_PRICE = "retail_price";
	private static final String SORT_BY_CATEGORY_NAME = "category_name";
	private static final String SORT_BY_VENDOR_NAME = "vendor_name";
	private static final String SORT_BY_DESC = "desc";

	public Product insert(String tenantId, Product product) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);

		product.setPartitionKey(Product.buildPartitionKey(tenantId));
		mapper.save(product);

		return get(tenantId, product.getId());
	}

	public Product get(String tenantId, String productId) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);

		Product model = mapper.load(Product.class, Product.buildPartitionKey(tenantId), productId);
		if (model == null) {
			throw new ProductNotFoundException(productId, Product.STORE_ID);
		}
		return model;
	}

	public List<Product> getCategoryProducts(String tenantId, String categoryId) {
		ProductSearchResponse response = search(tenantId, 0, 0, categoryId, null, null, null, new ArrayList<>());
		return response.getProducts();
	}

	public List<Product> getVendorProducts(String tenantId, String vendorId) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);

		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(PARTITION_KEY_PLACEHOLDER, new AttributeValue().withS(Product.buildPartitionKey(tenantId)));
		eav.put(VENDOR_ID_PLACEHOLDER, new AttributeValue().withS(vendorId));
		DynamoDBQueryExpression<Product> query = new DynamoDBQueryExpression<Product>()
				.withIndexName(Product.DbIndexNames.VENDOR_INDEX)
				.withConsistentRead(false)
				.withKeyConditionExpression(String.format("%s = %s AND %s = %s",
						Product.DbAttrNames.PARTITION_KEY, PARTITION_KEY_PLACEHOLDER,
						Product.DbAttrNames.VENDOR_ID, VENDOR_ID_PLACEHOLDER))
				.withExpressionAttributeValues(eav);

		return mapper.query(Product.class, query);
	}

	public List<Product> getProductBySKU(String tenantId, String sku) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);

		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(PARTITION_KEY_PLACEHOLDER, new AttributeValue().withS(Product.buildPartitionKey(tenantId)));
		eav.put(SKU_PLACEHOLDER, new AttributeValue().withS(sku));
		DynamoDBQueryExpression<Product> query = new DynamoDBQueryExpression<Product>()
				.withIndexName(Product.DbIndexNames.SKU_INDEX)
				.withConsistentRead(false)
				.withKeyConditionExpression(String.format("%s = %s AND %s = %s",
						Product.DbAttrNames.PARTITION_KEY, PARTITION_KEY_PLACEHOLDER,
						Product.DbAttrNames.SKU, SKU_PLACEHOLDER))
				.withExpressionAttributeValues(eav);

		return mapper.query(Product.class, query);
	}

	public ProductSearchResponse search(String tenantId,
										int from,
										int size,
										String categoryId,
										String vendorId,
										String filter,
										String sortBy,
										List<String> productIds) {
		try {
			TermQuery partitionKeyQuery = new TermQuery.Builder()
					.field(Product.OpenSearch.FieldNames.PARTITION_KEY_KEYWORD)
					.value(FieldValue.of(Product.buildPartitionKey(tenantId)))
					.build();

			List<Query> idQueries = new ArrayList<>();
			idQueries.add(partitionKeyQuery._toQuery());

			if (StringUtils.hasLength(categoryId)) {
				TermQuery categoryIdQuery = new TermQuery.Builder()
						.field(Product.OpenSearch.FieldNames.CATEGORY_ID_KEYWORD)
						.value(FieldValue.of(categoryId))
						.build();
				idQueries.add(categoryIdQuery._toQuery());
			}

			if (StringUtils.hasLength(vendorId)) {
				TermQuery vendorIdQuery = new TermQuery.Builder()
						.field(Product.OpenSearch.FieldNames.VENDOR_ID_KEYWORD)
						.value(FieldValue.of(vendorId))
						.build();
				idQueries.add(vendorIdQuery._toQuery());
			}

			List<Query> searchQueries = new ArrayList<>();
			int minShouldMatch = 0;
			if (StringUtils.hasLength(filter) || !productIds.isEmpty()) {
				MatchBoolPrefixQuery nameQuery = new MatchBoolPrefixQuery.Builder()
						.field(Product.OpenSearch.FieldNames.NAME)
						.query(filter)
						.build();
				MatchBoolPrefixQuery skuQuery = new MatchBoolPrefixQuery.Builder()
						.field(Product.OpenSearch.FieldNames.SKU)
						.query(filter)
						.build();
				MatchBoolPrefixQuery categoryNameQuery = new MatchBoolPrefixQuery.Builder()
						.field(Product.OpenSearch.FieldNames.CATEGORY_NAME)
						.query(filter)
						.build();
				MatchBoolPrefixQuery vendorNameQuery = new MatchBoolPrefixQuery.Builder()
						.field(Product.OpenSearch.FieldNames.VENDOR_NAME)
						.query(filter)
						.build();

				for (String pId : productIds) {
					MatchBoolPrefixQuery pIdQuery = new MatchBoolPrefixQuery.Builder()
							.field(Product.OpenSearch.FieldNames.SORT_KEY)
							.query(pId)
							.build();
					searchQueries.add(pIdQuery._toQuery());
				}
				searchQueries.add(nameQuery._toQuery());
				searchQueries.add(skuQuery._toQuery());
				searchQueries.add(categoryNameQuery._toQuery());
				searchQueries.add(vendorNameQuery._toQuery());
				minShouldMatch = 1;
			}

			List<SortOptions> sorting = new ArrayList<>();
			if (StringUtils.hasLength(sortBy)) {
				String field = getOpenSearchSortField(sortBy);
				if (StringUtils.hasLength(field)) {
					SortOrder order = sortBy.contains(SORT_BY_DESC) ? SortOrder.Desc : SortOrder.Asc;
					FieldSort fieldSort = FieldSort.of(f -> f.field(field).order(order));
					sorting.add(SortOptions.of(s -> s.field(fieldSort)));
				}
			}

			BoolQuery boolQuery = new BoolQuery.Builder()
					.should(searchQueries)
					.minimumShouldMatch(String.valueOf(minShouldMatch))
					.filter(idQueries)
					.build();

			SearchRequest req = SearchRequest.of(s -> s
					.index(Product.OpenSearch.getIndex(tenantId))
					.query(boolQuery._toQuery())
					.sort(sorting)
					.from(from)
					.size(size));
			SearchResponse<JsonNode> results = openSearchClient.search(req, JsonNode.class);

			ProductSearchResponse response = new ProductSearchResponse();
			response.setProducts(convertSearchResultsToModels(results, Product.class, tenantId));
			response.setCount(results.hits().total().value());
			return response;

		} catch (IOException e) {
			logger.error("Error reading from OpenSearch: ", e);
			throw new ProductSearchException(tenantId, from, size, categoryId, vendorId, filter, sortBy);
		}
	}

	public Product update(String tenantId, Product product) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);

		Product model = get(tenantId, product.getId());

		if (StringUtils.hasLength(product.getName())) {
			model.setName(product.getName());
		}

		if (StringUtils.hasLength(product.getDescription())) {
			model.setDescription(product.getDescription());
		}

		if (StringUtils.hasLength(product.getSku())) {
			if (!product.getSku().equals(model.getSku())) {
				checkProductExistence(tenantId, product.getSku());
			}
			model.setSku(product.getSku());
		}

		if (StringUtils.hasLength(product.getCategoryId())
				&& !model.getCategoryId().equals(product.getCategoryId())) {
			model.setCategoryId(product.getCategoryId());
			model.setCategoryPath(product.getCategoryPath());
		}

		if (StringUtils.hasLength(product.getCategoryName())) {
			model.setCategoryName(product.getCategoryName());
		}

		if (StringUtils.hasLength(product.getCategoryPath())) {
			model.setCategoryPath(product.getCategoryPath());
		}

		if (product.getQuantityOnHand() != null && product.getQuantityOnHand() >= 0) {
			model.setQuantityOnHand(product.getQuantityOnHand());
		}

		if (product.getMinQtyOnHand() != null && product.getMinQtyOnHand() >= 0) {
			model.setMinQtyOnHand(product.getMinQtyOnHand());
		}

		if (product.getMaxQtyOnHand() != null && product.getMaxQtyOnHand() >= 0) {
			model.setMaxQtyOnHand(product.getMaxQtyOnHand());
		}

		if (product.getRetailPrice() != null && product.getRetailPrice().compareTo(BigDecimal.ZERO) >= 0) {
			model.setRetailPrice(product.getRetailPrice());
		}

		if (product.getCost() != null && product.getCost().compareTo(BigDecimal.ZERO) >= 0) {
			model.setCost(product.getCost());
		}

		if (StringUtils.hasLength(product.getVendorId())) {
			model.setVendorId(product.getVendorId());
		}

		if (StringUtils.hasLength(product.getType())) {
			model.setType(product.getType());
		}

		if (StringUtils.hasLength(product.getInventoryStatus())) {
			model.setInventoryStatus(product.getInventoryStatus());
		}

		if (StringUtils.hasLength(product.getTaxable())) {
			model.setTaxable(product.getTaxable());
		}

		if (product.getReturnsAllowed() != null) {
			model.setReturnsAllowed(product.getReturnsAllowed());
		}

		if (product.getAgeVerificationRequired() != null) {
			model.setAgeVerificationRequired(product.getAgeVerificationRequired());
		}

		if (StringUtils.hasLength(product.getStockingUomId())) {
			model.setStockingUomId(product.getStockingUomId());
		}

		if (StringUtils.hasLength(product.getQuantityUomId())) {
			model.setQuantityUomId(product.getQuantityUomId());
		}

		if (StringUtils.hasLength(product.getPricingUomId())) {
			model.setPricingUomId(product.getPricingUomId());
		}

		if (StringUtils.hasLength(product.getVendorName())) {
			model.setVendorName(product.getVendorName());
		}

		model.setModified(new Date());

		mapper.save(model);

		return model;
	}

	public void delete(String tenantId, String id) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);

		Product model = get(tenantId, id);

		mapper.delete(model);
	}

	public void batchUpdate(String tenantId, List<Product> productsToUpdate) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);

		List<Object> objectsToWrite = new ArrayList<>(productsToUpdate);
		mapper.batchWrite(objectsToWrite, new ArrayList<>());
	}

	public void checkProductExistence(String tenantId, String sku) {
		List<Product> products = getProductBySKU(tenantId, sku);
		if (!products.isEmpty()) {
			throw new ProductExistsException(products.get(0).getId(),
					String.format("There is a product with same SKU '%s'", sku), tenantId, Product.STORE_ID);
		}
	}

	public List<Product> batchLoad(String tenantId, List<String> productIds) {
		DynamoDBMapper mapper = dynamoDBMapper(tenantId);

		List<KeyPair> keyPairs = new ArrayList<>();
		for (String id : productIds) {
			KeyPair pair = new KeyPair();
			pair.setHashKey(Product.buildPartitionKey(tenantId));
			pair.setRangeKey(id);
			keyPairs.add(pair);
		}
		Map<Class<?>, List<KeyPair>> tableKeyPair = new HashMap<>();
		tableKeyPair.put(Product.class, keyPairs);

		Map<String, List<Object>> batchResults = mapper.batchLoad(tableKeyPair);
		if (batchResults.isEmpty()) {
			logger.info("No products matching the IDs: {}", productIds);
			return new ArrayList<>();
		}
		return (List<Product>) (List<?>) batchResults.get(buildTableName(tenantId));
	}

	public List<Product> searchByIdentifier(String tenantId, String identifier) {
		List<Product> products;
		List<Query> mustQueries = new ArrayList<>();
		MatchQuery pKeyQuery = QueryBuilders.match()
				.field(Product.OpenSearch.FieldNames.PARTITION_KEY)
				.query(FieldValue.of(Product.buildPartitionKey(tenantId)))
				.build();
		mustQueries.add(pKeyQuery._toQuery());

		MatchQuery skuQuery = QueryBuilders.match()
				.field(Product.OpenSearch.FieldNames.SKU)
				.query(FieldValue.of(identifier))
				.build();
		mustQueries.add(skuQuery._toQuery());

		BoolQuery boolQuery = new BoolQuery.Builder().must(mustQueries).build();

		SearchRequest req = SearchRequest.of(s -> s
				.index(Product.OpenSearch.getIndex(tenantId))
				.query(boolQuery._toQuery())
		);

		try {
			SearchResponse<JsonNode> results = openSearchClient.search(req, JsonNode.class);
			products = convertSearchResultsToModels(results, Product.class, tenantId);
		} catch (IOException e) {
			logger.error("Error reading from OpenSearch: ", e);
			throw new SearchException("Error reading from OpenSearch");
		}

		return products;
	}

	private String getOpenSearchSortField(String sortBy) {
		if (sortBy.toLowerCase().contains(SORT_BY_CATEGORY_NAME)) {
			return Product.OpenSearch.FieldNames.CATEGORY_NAME_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_VENDOR_NAME)) {
			return Product.OpenSearch.FieldNames.VENDOR_NAME_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_NAME)) {
			return Product.OpenSearch.FieldNames.NAME_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_SKU)) {
			return Product.OpenSearch.FieldNames.SKU_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_RETAIL_PRICE)) {
			return Product.OpenSearch.FieldNames.RETAIL_PRICE_KEYWORD;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_QUANTITY_ON_HAND)) {
			return Product.OpenSearch.FieldNames.QOH_KEYWORD;
		}

		return "";
	}
}

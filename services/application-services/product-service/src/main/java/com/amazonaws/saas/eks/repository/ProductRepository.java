package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.ProductExistsException;
import com.amazonaws.saas.eks.exception.ProductNotFoundException;
import com.amazonaws.saas.eks.exception.ProductSearchException;
import com.amazonaws.saas.eks.model.EntityType;
import com.amazonaws.saas.eks.model.Product;
import com.amazonaws.saas.eks.model.DynamoDbStreamRecord;
import com.amazonaws.saas.eks.model.ProductSearchResponse;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.*;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
	private static final Logger logger = LogManager.getLogger(ProductRepository.class);

	// TODO: MOVE TO CONFIG OR INPUT
	private static final String STORE_ID = "store1";

	private static final String OPEN_SEARCH_INDEX = "products-index";
	private static final String OPEN_SEARCH_PARTITION_KEY = "NewImage.PartitionKey.S";
	private static final String OPEN_SEARCH_CATEGORY_ID = "NewImage.CategoryID.S";
	private static final String OPEN_SEARCH_VENDOR_ID = "NewImage.VendorID.S";
	private static final String OPEN_SEARCH_NAME = "NewImage.Name.S";
	private static final String OPEN_SEARCH_SKU = "NewImage.SKU.S";
	private static final String OPEN_SEARCH_SORT_NAME = "NewImage.Name.S.keyword";
	private static final String OPEN_SEARCH_SORT_SKU = "NewImage.SKU.S.keyword";
	private static final String OPEN_SEARCH_SORT_QOH = "NewImage.QuantityOnHand.N.keyword";
	private static final String OPEN_SEARCH_SORT_RETAIL_PRICE = "NewImage.RetailPrice.N.keyword";
	private static final String OPEN_SEARCH_SORT_CATEGORY_NAME = "NewImage.CategoryName.S.keyword";
	private static final String OPEN_SEARCH_SORT_VENDOR_NAME = "NewImage.VendorName.S.keyword";

	private static final String SORT_BY_NAME = "name";
	private static final String SORT_BY_SKU = "sku";
	private static final String SORT_BY_QUANTITY_ON_HAND = "qty_on_hand";
	private static final String SORT_BY_RETAIL_PRICE = "retail_price";
	private static final String SORT_BY_CATEGORY_NAME = "category_name";
	private static final String SORT_BY_VENDOR_NAME = "vendor_name";
	private static final String SORT_BY_DESC = "desc";

	@Autowired
	private DynamoDBMapper mapper;

	@Autowired
	private OpenSearchClient openSearchClient;

	public Product insert(String tenantId, Product product) {
		product.setPartitionKey(getPartitionKey(tenantId));
		mapper.save(product);

		return get(tenantId, product.getId());
	}

	public Product get(String tenantId, String productId) {
		Product model = mapper.load(Product.class, getPartitionKey(tenantId), productId);
		if (model == null) {
			throw new ProductNotFoundException(productId, STORE_ID);
		}
		return model;
	}

	public List<Product> getCategoryProducts(String tenantId, String categoryId) {
		ProductSearchResponse response = search(tenantId, 0, 0, categoryId, null, null, null);
		return response.getProducts();
	}

	public List<Product> getVendorProducts(String tenantId, String vendorId) {
		ProductSearchResponse response = search(tenantId, 0, 0, null, vendorId, null, null);
		return response.getProducts();
	}

	public List<Product> getProductBySKU(String tenantId, String sku) {
		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(":partitionKey", new AttributeValue().withS(getPartitionKey(tenantId)));
		eav.put(":sku", new AttributeValue().withS(sku));
		DynamoDBQueryExpression<Product> query = new DynamoDBQueryExpression<Product>()
				.withIndexName(Product.SKU_INDEX)
				.withConsistentRead(false)
				.withKeyConditionExpression(String.format("%s = :partitionKey AND %s = :sku",
						Product.PARTITION_KEY, Product.SKU))
				.withExpressionAttributeValues(eav);

		return mapper.query(Product.class, query);
	}

	public ProductSearchResponse search(String tenantId,
										int from,
										int size,
										String categoryId,
										String vendorId,
										String filter,
										String sortBy) {
		try {
			MatchQuery partitionKeyQuery = new MatchQuery.Builder().field(OPEN_SEARCH_PARTITION_KEY)
					.query(FieldValue.of(getPartitionKey(tenantId))).build();

			List<Query> idQueries = new ArrayList<>();
			idQueries.add(partitionKeyQuery._toQuery());

			if (!StringUtils.isEmpty(categoryId)) {
				MatchQuery categoryIdQuery = new MatchQuery.Builder().field(OPEN_SEARCH_CATEGORY_ID)
						.query(FieldValue.of(categoryId)).build();
				idQueries.add(categoryIdQuery._toQuery());
			}

			if (!StringUtils.isEmpty(vendorId)) {
				MatchQuery vendorIdQuery = new MatchQuery.Builder().field(OPEN_SEARCH_VENDOR_ID)
						.query(FieldValue.of(vendorId)).build();
				idQueries.add(vendorIdQuery._toQuery());
			}

			List<Query> searchQueries = new ArrayList<>();
			int minShouldMatch = 0;
			if (!StringUtils.isEmpty(filter)) {
				MatchQuery nameQuery = new MatchQuery.Builder().field(OPEN_SEARCH_NAME)
						.query(FieldValue.of(filter)).build();
				MatchQuery skuQuery = new MatchQuery.Builder().field(OPEN_SEARCH_SKU)
						.query(FieldValue.of(filter)).build();
				searchQueries.add(nameQuery._toQuery());
				searchQueries.add(skuQuery._toQuery());
				minShouldMatch = 1;
			}

			List<SortOptions> sorting = new ArrayList<>();
			if (!StringUtils.isEmpty(sortBy)) {
				String field = getOpenSearchSortField(sortBy);
				if (!StringUtils.isEmpty(field)) {
					SortOrder order = sortBy.contains(SORT_BY_DESC) ? SortOrder.Desc : SortOrder.Asc;
					FieldSort fieldSort = FieldSort.of(f -> f.field(field).order(order));
					sorting.add(SortOptions.of(s -> s.field(fieldSort)));
				}
			}

			BoolQuery boolQuery = new BoolQuery.Builder()
					.must(idQueries)
					.should(searchQueries)
					.minimumShouldMatch(String.valueOf(minShouldMatch))
					.build();

			SearchRequest req = SearchRequest.of(s -> s
					.index(OPEN_SEARCH_INDEX)
					.query(boolQuery._toQuery())
					.sort(sorting)
					.from(from)
					.size(size));
			SearchResponse<JsonNode> results = openSearchClient.search(req, JsonNode.class);

			ProductSearchResponse response = new ProductSearchResponse();
			response.setProducts(convertSearchResultsToProducts(results));
			response.setCount(results.hits().total().value());
			return response;
			
		} catch (IOException e) {
			logger.error("Error reading from OpenSearch: ", e);
			throw new ProductSearchException(tenantId, from, size, categoryId, vendorId, filter, sortBy);
		}
	}

	public Product update(String tenantId, Product product) {
		Product model = get(tenantId, product.getId());

		if (!StringUtils.isEmpty(product.getName())) {
			model.setName(product.getName());
		}

		if (!StringUtils.isEmpty(product.getDescription())) {
			model.setDescription(product.getDescription());
		}

		if (!StringUtils.isEmpty(product.getSku())) {
			if (!product.getSku().equals(model.getSku())) {
				checkProductExistence(tenantId, product.getSku());
			}
			model.setSku(product.getSku());
		}

		if (!StringUtils.isEmpty(product.getCategoryId())
				&& !model.getCategoryId().equals(product.getCategoryId())) {
			model.setCategoryId(product.getCategoryId());
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

		if (!StringUtils.isEmpty(product.getVendorId())) {
			model.setVendorId(product.getVendorId());
		}

		if (!StringUtils.isEmpty(product.getType())) {
			model.setType(product.getType());
		}

		if (!StringUtils.isEmpty(product.getInventoryStatus())) {
			model.setInventoryStatus(product.getInventoryStatus());
		}

		if (!StringUtils.isEmpty(product.getTaxable())) {
			model.setTaxable(product.getTaxable());
		}

		if (product.getReturnsAllowed() != null) {
			model.setReturnsAllowed(product.getReturnsAllowed());
		}

		if (product.getAgeVerificationRequired() != null) {
			model.setAgeVerificationRequired(product.getAgeVerificationRequired());
		}

		if (!StringUtils.isEmpty(product.getStockingUomId())) {
			model.setStockingUomId(product.getStockingUomId());
		}

		if (!StringUtils.isEmpty(product.getQuantityUomId())) {
			model.setStockingUomId(product.getQuantityUomId());
		}

		if (!StringUtils.isEmpty(product.getPricingUomId())) {
			model.setStockingUomId(product.getPricingUomId());
		}

		model.setModified(new Date());

		mapper.save(model);

		return model;
	}

	public void delete(String tenantId, String id) {
		Product model = get(tenantId, id);

		mapper.delete(model);
	}

	public void batchUpdate(List<Product> productsToUpdate) {
		List<Object> objectsToWrite = new ArrayList<>(productsToUpdate);
		mapper.batchWrite(objectsToWrite, new ArrayList<>());
	}

	public void checkProductExistence(String tenantId, String sku) {
		List<Product> products = getProductBySKU(tenantId, sku);
		if (!products.isEmpty()) {
			throw new ProductExistsException(products.get(0).getId(),
					String.format("There is a product with same SKU '%s'", sku), tenantId, STORE_ID);
		}
	}

	private String getPartitionKey(String tenantId) {
		return String.format("%s%s%s%s%s", tenantId, Product.KEY_DELIMITER, STORE_ID, Product.KEY_DELIMITER,
				EntityType.PRODUCTS.getLabel());
	}

	private List<Product> convertSearchResultsToProducts(SearchResponse<JsonNode> results) {
		ObjectMapper objectMapper = new ObjectMapper()
				.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

		List<DynamoDbStreamRecord> records = new ArrayList<>();
		for(int i = 0; i < results.hits().hits().size(); i++) {
			DynamoDbStreamRecord r = objectMapper.convertValue(results.hits().hits().get(i).source(), DynamoDbStreamRecord.class);
			records.add(r);
		}

		List<Map<String, AttributeValue>> dynamoDbProductAttributes = records
				.stream()
				.map(DynamoDbStreamRecord::getNewImage)
				.collect(Collectors.toList());
		return mapper.marshallIntoObjects(Product.class, dynamoDbProductAttributes);
	}

	private String getOpenSearchSortField(String sortBy) {
		if (sortBy.toLowerCase().contains(SORT_BY_CATEGORY_NAME)) {
			return OPEN_SEARCH_SORT_CATEGORY_NAME;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_VENDOR_NAME)) {
			return OPEN_SEARCH_SORT_VENDOR_NAME;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_NAME)) {
			return OPEN_SEARCH_SORT_NAME;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_SKU)) {
			return OPEN_SEARCH_SORT_SKU;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_RETAIL_PRICE)) {
			return OPEN_SEARCH_SORT_RETAIL_PRICE;
		}
		if (sortBy.toLowerCase().contains(SORT_BY_QUANTITY_ON_HAND)) {
			return OPEN_SEARCH_SORT_QOH;
		}

		return "";
	}
}

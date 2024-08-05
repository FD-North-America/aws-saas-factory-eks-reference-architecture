package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.*;
import com.amazonaws.saas.eks.model.enums.ReportName;
import com.amazonaws.saas.eks.order.model.CategorySale;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.product.model.Category;
import com.amazonaws.saas.eks.product.model.Product;
import com.amazonaws.saas.eks.product.model.UOM;
import com.amazonaws.saas.eks.product.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.amazonaws.saas.eks.util.Utils.convertToSimpleQueryDate;

@Repository
public class CategorySalesRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(CategorySalesRepository.class);
    public static final String NEW_IMAGE_SORT_KEY_S_KEYWORD = "NewImage.SortKey.S.keyword";
    public static final int MIN_SHOULD_MATCH = 1;

    public List<CategorySale> getCategorySales(String tenantId, ZonedDateTime fromDate, ZonedDateTime toDate) {
        List<Query> mustQueries = new ArrayList<>();

        RangeQuery dateQuery = QueryBuilders.range()
                .field(CategorySale.OpenSearch.FieldNames.CREATED)
                .gte(JsonData.of(convertToSimpleQueryDate(fromDate)))
                .lte(JsonData.of(convertToSimpleQueryDate(toDate)))
                .build();
        mustQueries.add(dateQuery._toQuery());

        BoolQuery boolQuery = QueryBuilders.bool()
                .must(mustQueries)
                .build();

        int numberOfCategorySales;
        try {
            numberOfCategorySales = getOpenSearchItemCount(CategorySale.OpenSearch.getIndex(tenantId), boolQuery._toQuery());
        } catch (Exception ex) {
            logger.error("Error fetching data size for category sales report: ", ex);
            throw new ReportGenerationException(ReportName.CATEGORY_SALES.toString(), tenantId);
        }

        SearchRequest req = SearchRequest.of(s -> s
                .index(CategorySale.OpenSearch.getIndex(tenantId))
                .query(boolQuery._toQuery())
                .from(0)
                .size(numberOfCategorySales));

        SearchResponse<JsonNode> results;
        try {
            results = openSearchClient.search(req, JsonNode.class);
        } catch (Exception ex) {
            logger.error("Error fetching category sales for category sale report", ex);
            throw new ReportGenerationException(ReportName.CATEGORY_SALES.toString(), tenantId);
        }

        return convertSearchResultsToModels(results, CategorySale.class);
    }

    public List<Category> getAllCategories(String tenantId) {
        DynamoDBMapper mapper = dynamoDBProductMapper(tenantId);
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":partitionKey", new AttributeValue().withS(getPartitionKey(tenantId, EntityType.CATEGORIES.getLabel())));
        DynamoDBQueryExpression<Category> query = new DynamoDBQueryExpression<Category>()
                .withKeyConditionExpression("PartitionKey = :partitionKey")
                .withExpressionAttributeValues(eav);
        PaginatedQueryList<Category> categories = mapper.query(Category.class, query);

        return new ArrayList<>(categories);
    }

    public List<Order> getOrders(String tenantId, Set<String> orderIds) {
        SearchResponse<JsonNode> results = search(Order.OpenSearch.getIndex(tenantId), orderIds);
        return convertSearchResultsToModels(results, Order.class);
    }

    public List<Product> getProducts(String tenantId, Set<String> productIds) {
        List<Query> filterQueries = new ArrayList<>();
        TermQuery partitionKeyQuery = new TermQuery.Builder()
                .field(Product.OpenSearch.FieldNames.PARTITION_KEY_KEYWORD)
                .value(FieldValue.of(getPartitionKey(tenantId, EntityType.PRODUCTS.getLabel())))
                .build();
        filterQueries.add(partitionKeyQuery._toQuery());

        SearchResponse<JsonNode> results = search("products-index", productIds, filterQueries);
        return convertSearchResultsToModels(results, Product.class);
    }

    public Map<String, UOM> getUOM(String tenantId, Set<String> uomIds) {
        List<Query> filterQueries = new ArrayList<>();
        TermQuery partitionKeyQuery = new TermQuery.Builder()
                .field(Product.OpenSearch.FieldNames.PARTITION_KEY_KEYWORD)
                .value(FieldValue.of(getPartitionKey(tenantId, EntityType.UOM.getLabel())))
                .build();
        filterQueries.add(partitionKeyQuery._toQuery());

        SearchResponse<JsonNode> results = search("uom-index", uomIds, filterQueries);
        return convertSearchResultsToModels(results, UOM.class)
                .stream()
                .collect(Collectors.toMap(UOM::getId, u->u));
    }

    private SearchResponse<JsonNode> search(String openSearchIndex, Set<String> itemIds) {
        return search(openSearchIndex, itemIds, new ArrayList<>());
    }

    private SearchResponse<JsonNode> search(String openSearchIndex, Set<String> itemIds, List<Query> filterQueries) {
        List<FieldValue> fieldValues = new ArrayList<>();
        for (String itemId : itemIds) {
            fieldValues.add(FieldValue.of(itemId));
        }

        TermsQuery termsQuery = new TermsQuery.Builder()
                .field(NEW_IMAGE_SORT_KEY_S_KEYWORD)
                .terms(new TermsQueryField.Builder().value(fieldValues).build()).build();

        BoolQuery boolQuery = new BoolQuery.Builder()
                .should(termsQuery._toQuery())
                .minimumShouldMatch(String.valueOf(MIN_SHOULD_MATCH))
                .filter(filterQueries)
                .build();

        int numberOfItems;
        try {
            numberOfItems = getOpenSearchItemCount(openSearchIndex, boolQuery._toQuery());
        } catch (Exception ex) {
            logger.error("Error fetching data size for items", ex);
            throw new OrderException("Error fetching data size for items");
        }

        SearchRequest req = SearchRequest.of(s -> s
                .index(openSearchIndex)
                .query(boolQuery._toQuery())
                .from(0)
                .size(numberOfItems));

        SearchResponse<JsonNode> results;
        try {
            results = openSearchClient.search(req, JsonNode.class);
        } catch (Exception ex) {
            logger.error("Error fetching items: ", ex);
            throw new OrderException("Error fetching items");
        }
        return results;
    }

    private String getPartitionKey(String tenantId, String entity) {
        return String.format("%s%s%s%s%s", tenantId, Product.KEY_DELIMITER, Product.STORE_ID, Product.KEY_DELIMITER,
                entity);
    }
}

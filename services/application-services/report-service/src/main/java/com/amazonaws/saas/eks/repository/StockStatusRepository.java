package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.ReportGenerationException;
import com.amazonaws.saas.eks.model.StockLevelData;
import com.amazonaws.saas.eks.model.enums.ProductInventoryStatus;
import com.amazonaws.saas.eks.model.enums.ReportName;
import com.amazonaws.saas.eks.model.enums.StockLevel;
import com.amazonaws.saas.eks.product.model.Product;
import com.amazonaws.saas.eks.product.model.UOM;
import com.amazonaws.saas.eks.product.model.enums.EntityType;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class StockStatusRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(StockStatusRepository.class);

    private static final String OPEN_SEARCH_PRODUCTS_INDEX = "products-index";
    private static final String OPEN_SEARCH_UOM_INDEX = "uom-index";
    private static final String NEGATIVE_PREFIX = "-";
    private static final String ZERO_PREFIX = "0";

    public StockLevelData getStockStatusData(String tenantId,
                                             int from,
                                             int size,
                                             List<String> categoryIds,
                                             StockLevel stockLevel,
                                             String itemStatus,
                                             String vendor) {
        int reportSize;
        TermQuery partitionKeyQuery = new TermQuery.Builder()
                .field(Product.OpenSearch.FieldNames.PARTITION_KEY_KEYWORD)
                .value(FieldValue.of(getPartitionKey(tenantId, EntityType.PRODUCTS.getLabel())))
                .build();

        List<Query> filterQueries = new ArrayList<>();
        filterQueries.add(partitionKeyQuery._toQuery());


        List<Query> mustQueries = new ArrayList<>();

        // Category filters
        List<Query> categoryQueries = new ArrayList<>();
        int minShouldMatch = 0;
        if (!categoryIds.isEmpty()) {
            for (String id : categoryIds) {
                MatchQuery idQuery = new MatchQuery.Builder()
                        .field(Product.OpenSearch.FieldNames.CATEGORY_ID)
                        .query(FieldValue.of(id))
                        .build();
                categoryQueries.add(idQuery._toQuery());
            }
            minShouldMatch = 1;
        }

        // Item Status
        if (StringUtils.hasLength(itemStatus) && !itemStatus.equals(ProductInventoryStatus.ALL.toString())) {
            MatchQuery itemStatusQuery = new MatchQuery.Builder()
                    .field(Product.OpenSearch.FieldNames.ITEM_STATUS)
                    .query(FieldValue.of(itemStatus))
                    .build();
            mustQueries.add(itemStatusQuery._toQuery());
        }

        // Vendor
        if (StringUtils.hasLength(vendor)) {
            MatchQuery vendorQuery = new MatchQuery.Builder()
                    .field(Product.OpenSearch.FieldNames.VENDOR_NAME)
                    .query(FieldValue.of(vendor))
                    .build();
            mustQueries.add(vendorQuery._toQuery());
        }

        // Stock Level
        List<Query> mustNotQueries = new ArrayList<>();
        if (stockLevel != null && stockLevel != StockLevel.ALL) {
            if (stockLevel == StockLevel.NEGATIVE || stockLevel == StockLevel.ZERO) {
                String prefix = stockLevel == StockLevel.NEGATIVE ? NEGATIVE_PREFIX : ZERO_PREFIX;
                MatchBoolPrefixQuery stockLevelQuery = new MatchBoolPrefixQuery.Builder()
                        .field(Product.OpenSearch.FieldNames.QOH_KEYWORD)
                        .query(prefix).build();
                mustQueries.add(stockLevelQuery._toQuery());
            } else {
                // Filtering for positive values by excluding any negative or zero values
                MatchBoolPrefixQuery stockLevelNegativeQuery = new MatchBoolPrefixQuery.Builder()
                        .field(Product.OpenSearch.FieldNames.QOH_KEYWORD)
                        .query(NEGATIVE_PREFIX)
                        .build();
                MatchBoolPrefixQuery stockLevelZeroQuery = new MatchBoolPrefixQuery.Builder()
                        .field(Product.OpenSearch.FieldNames.QOH_KEYWORD)
                        .query(ZERO_PREFIX)
                        .build();
                mustNotQueries.add(stockLevelNegativeQuery._toQuery());
                mustNotQueries.add(stockLevelZeroQuery._toQuery());
            }
        }

        BoolQuery boolQuery = new BoolQuery.Builder()
                .must(mustQueries)
                .should(categoryQueries)
                .mustNot(mustNotQueries)
                .minimumShouldMatch(String.valueOf(minShouldMatch))
                .filter(filterQueries)
                .build();

        if (size == 0) {
            try {
                reportSize = getOpenSearchItemCount(OPEN_SEARCH_PRODUCTS_INDEX, boolQuery._toQuery());
            } catch (Exception ex) {
                logger.error("Error fetching data size for stock level report: ", ex);
                throw new ReportGenerationException(ReportName.STOCK_STATUS.toString(), tenantId);
            }
        } else {
            reportSize = size;
        }

        SearchRequest req = SearchRequest.of(s -> s
                .index(OPEN_SEARCH_PRODUCTS_INDEX)
                .query(boolQuery._toQuery())
                .from(from)
                .size(reportSize));
        SearchResponse<JsonNode> results = null;

        try {
            results = openSearchClient.search(req, JsonNode.class);
        } catch (Exception ex) {
            logger.error("Error fetching data for stock level report: ", ex);
            throw new ReportGenerationException(ReportName.STOCK_STATUS.toString(), tenantId);
        }

        StockLevelData data = new StockLevelData();
        data.setProducts(convertSearchResultsToModels(results, Product.class));
        data.setCount(results.hits().total().value());
        return data;
    }

    public Map<String, UOM> getUom(String tenantId) {
        List<Query> mustQueries = new ArrayList<>();

        TermQuery partitionKeyQuery = new TermQuery.Builder()
                .field(Product.OpenSearch.FieldNames.PARTITION_KEY_KEYWORD)
                .value(FieldValue.of(getPartitionKey(tenantId, EntityType.UOM.getLabel())))
                .build();
        mustQueries.add(partitionKeyQuery._toQuery());

        BoolQuery boolQuery = new BoolQuery.Builder()
                .filter(mustQueries)
                .build();

        int reportSize;
        try {
            reportSize = getOpenSearchItemCount(OPEN_SEARCH_UOM_INDEX, boolQuery._toQuery());
        } catch (Exception ex) {
            logger.error("Error fetching uom data size for stock level report: ", ex);
            throw new ReportGenerationException(ReportName.STOCK_STATUS.toString(), tenantId);
        }

        SearchRequest req = SearchRequest.of(s -> s
                .index(OPEN_SEARCH_UOM_INDEX)
                .query(boolQuery._toQuery())
                .from(0)
                .size(reportSize));
        SearchResponse<JsonNode> results = null;

        try {
            results = openSearchClient.search(req, JsonNode.class);
        } catch (Exception ex) {
            logger.error("Error fetching uom data for stock level report: ", ex);
            throw new ReportGenerationException(ReportName.STOCK_STATUS.toString(), tenantId);
        }

        List<UOM> uomList = convertSearchResultsToModels(results, UOM.class);
        return uomList.stream().collect(Collectors.toMap(UOM::getId, u -> u));
    }

    public Map<String, UOM> getUOMs(String tenantId, List<String> productsIds) {
        List<Query> mustQueries = new ArrayList<>();

        TermQuery partitionKeyQuery = new TermQuery.Builder()
                .field(Product.OpenSearch.FieldNames.PARTITION_KEY_KEYWORD)
                .value(FieldValue.of(getPartitionKey(tenantId, EntityType.UOM.getLabel()))).build();
        mustQueries.add(partitionKeyQuery._toQuery());

        // Product filter
        List<Query> productQueries = new ArrayList<>();
        int minShouldMatch = 0;
        if (!productsIds.isEmpty()) {
            for (String id : productsIds) {
                MatchQuery idQuery = new MatchQuery.Builder().field(Product.OpenSearch.FieldNames.PRODUCT_ID)
                        .query(FieldValue.of(id)).build();
                productQueries.add(idQuery._toQuery());
            }
            minShouldMatch = 1;
        }

        BoolQuery boolQuery = new BoolQuery.Builder()
                .must(mustQueries)
                .should(productQueries)
                .minimumShouldMatch(String.valueOf(minShouldMatch))
                .build();

        int reportSize;
        try {
            reportSize = getOpenSearchItemCount(OPEN_SEARCH_UOM_INDEX, boolQuery._toQuery());
        } catch (Exception ex) {
            logger.error("Error fetching uom data size for stock level report: ", ex);
            throw new ReportGenerationException(ReportName.STOCK_STATUS.toString(), tenantId);
        }

        SearchRequest req = SearchRequest.of(s -> s
                .index(OPEN_SEARCH_UOM_INDEX)
                .query(boolQuery._toQuery())
                .from(0)
                .size(reportSize));
        SearchResponse<JsonNode> results = null;

        try {
            results = openSearchClient.search(req, JsonNode.class);
        } catch (Exception ex) {
            logger.error("Error fetching uom data for stock level report: ", ex);
            throw new ReportGenerationException(ReportName.STOCK_STATUS.toString(), tenantId);
        }

        List<UOM> uomList = convertSearchResultsToModels(results, UOM.class);
        return uomList.stream().collect(Collectors.toMap(UOM::getId, u -> u));
    }

    private String getPartitionKey(String tenantId, String entity) {
        // TODO: REMOVE STORE FROM PARTITION KEY
        return String.format("%s#store1#%s", tenantId, entity);
    }
}

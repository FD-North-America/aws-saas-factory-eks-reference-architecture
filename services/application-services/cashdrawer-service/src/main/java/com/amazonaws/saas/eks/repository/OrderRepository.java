package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.CashDrawerException;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.order.model.enums.OrderStatus;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldSort;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.CountRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.amazonaws.saas.eks.util.Utils.convertToQueryDate;

@Repository
public class OrderRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(OrderRepository.class);

    @Autowired
    private OpenSearchClient openSearchClient;

    public List<Order> getOrdersByCashDrawer(String tenantId,
                                             String cashDrawerId,
                                             Date fromDate,
                                             Date toDate) {
        List<Query> mustQueries = new ArrayList<>();

        MatchQuery statusQuery = QueryBuilders.match()
                .field(Order.OpenSearch.FieldNames.STATUS)
                .query(FieldValue.of(OrderStatus.PAID.toString()))
                .build();
        mustQueries.add(statusQuery._toQuery());

        MatchQuery cashDrawerQuery = QueryBuilders.match()
                .field(Order.OpenSearch.FieldNames.CASH_DRAWER_ID)
                .query(FieldValue.of(cashDrawerId))
                .build();
        mustQueries.add(cashDrawerQuery._toQuery());

        RangeQuery paidDateQuery = QueryBuilders.range()
                .field(Order.OpenSearch.FieldNames.PAID_DATE)
                .gte(JsonData.of(convertToQueryDate(fromDate)))
                .lte(JsonData.of(convertToQueryDate(toDate)))
                .build();
        mustQueries.add(paidDateQuery._toQuery());

        BoolQuery boolQuery = QueryBuilders.bool()
                .must(mustQueries)
                .build();

        List<SortOptions> sortOptions = new ArrayList<>();
        FieldSort dateSort = FieldSort.of(f -> f.field(Order.OpenSearch.FieldNames.PAID_DATE).order(SortOrder.Desc));
        sortOptions.add(SortOptions.of(s -> s.field(dateSort)));

        int reportSize;
        try {
            Query query = boolQuery._toQuery();
            String index = Order.OpenSearch.getIndex(tenantId);
            CountRequest countRequest = CountRequest.of(c -> c.index(index).query(query));
            reportSize = (int) openSearchClient.count(countRequest).count();
            if (reportSize == 0) {
                return new ArrayList<>();
            }

            SearchRequest req = SearchRequest.of(s -> s
                    .index(Order.OpenSearch.getIndex(tenantId))
                    .query(query)
                    .sort(sortOptions)
                    .from(0)
                    .size(reportSize));
            SearchResponse<JsonNode> results;
            results = openSearchClient.search(req, JsonNode.class);
            return convertSearchResultsToModels(dynamoDBMapper(tenantId), results, Order.class);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Error reading from OpenSearch: %s", tenantId, e);
            logger.error(message);
            throw new CashDrawerException(message);
        }
    }
}

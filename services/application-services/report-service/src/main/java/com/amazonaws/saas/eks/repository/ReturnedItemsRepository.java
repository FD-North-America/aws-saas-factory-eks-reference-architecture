package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.cashdrawer.model.CashDrawer;
import com.amazonaws.saas.eks.cashdrawer.model.enums.CashDrawerStatus;
import com.amazonaws.saas.eks.exception.ReportGenerationException;
import com.amazonaws.saas.eks.model.enums.ReportName;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.order.model.enums.OrderStatus;
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
import java.util.ArrayList;
import java.util.List;

import static com.amazonaws.saas.eks.util.Utils.convertToQueryDate;

@Repository
public class ReturnedItemsRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(ReturnedItemsRepository.class);

    public List<Order> getOrdersWithReturns(String tenantId, ZonedDateTime from, ZonedDateTime to) {
        List<Query> mustQueries = new ArrayList<>();

        MatchQuery partitionKeyQuery = QueryBuilders.match()
                .field(Order.OpenSearch.FieldNames.PARTITION_KEY)
                .query(FieldValue.of(Order.OpenSearch.ENTITY))
                .build();
        mustQueries.add(partitionKeyQuery._toQuery());

        MatchQuery statusQuery = QueryBuilders.match()
                .field(Order.OpenSearch.FieldNames.STATUS)
                .query(FieldValue.of(OrderStatus.PAID.toString()))
                .build();
        mustQueries.add(statusQuery._toQuery());

        MatchQuery hasReturnsIdQuery = QueryBuilders.match()
                .field(Order.OpenSearch.FieldNames.HAS_RETURNS)
                .query(FieldValue.of(1))
                .build();
        mustQueries.add(hasReturnsIdQuery._toQuery());

        RangeQuery checkoutDateQuery = QueryBuilders.range()
                .field(Order.OpenSearch.FieldNames.CREATED)
                .gte(JsonData.of(convertToQueryDate(from)))
                .lte(JsonData.of(convertToQueryDate(to)))
                .build();
        mustQueries.add(checkoutDateQuery._toQuery());

        BoolQuery boolQuery = QueryBuilders.bool()
                .must(mustQueries)
                .build();

        int reportSize;
        try {
            reportSize = getOpenSearchItemCount(Order.OpenSearch.getIndex(tenantId), boolQuery._toQuery());
        } catch (Exception ex) {
            logger.error("Error fetching count of orders with returns", ex);
            throw new ReportGenerationException(ReportName.RETURNED_ITEMS.toString(), tenantId);
        }

        SearchRequest req = SearchRequest.of(s -> s
                .index(Order.OpenSearch.getIndex(tenantId))
                .query(boolQuery._toQuery())
                .from(0)
                .size(reportSize));

        SearchResponse<JsonNode> results;
        try {
            results = openSearchClient.search(req, JsonNode.class);
        } catch (Exception ex) {
            logger.error("Error fetching orders with returns: ", ex);
            throw new ReportGenerationException(ReportName.RETURNED_ITEMS.toString(), tenantId);
        }

        return convertSearchResultsToModels(results, Order.class);
    }

    public List<CashDrawer> getCashDrawersByAssignedUser(String tenantId, String assignedUser) {
        List<Query> mustQueries = new ArrayList<>();

        MatchQuery partitionKeyQuery = QueryBuilders.match()
                .field(CashDrawer.OpenSearch.FieldNames.PARTITION_KEY)
                .query(FieldValue.of(CashDrawer.OpenSearch.ENTITY))
                .build();
        mustQueries.add(partitionKeyQuery._toQuery());

        MatchQuery assignedUserQuery = QueryBuilders.match()
                .field(CashDrawer.OpenSearch.FieldNames.ASSIGNED_USER)
                .query(FieldValue.of(assignedUser))
                .build();
        mustQueries.add(assignedUserQuery._toQuery());

        MatchQuery statusQuery = QueryBuilders.match()
                .field(CashDrawer.OpenSearch.FieldNames.STATUS)
                .query(FieldValue.of(CashDrawerStatus.DELETED.toString()))
                .build();

        BoolQuery boolQuery = QueryBuilders.bool()
                .must(mustQueries)
                .mustNot(statusQuery._toQuery())
                .build();

        int reportSize;
        try {
            reportSize = getOpenSearchItemCount(CashDrawer.OpenSearch.getIndex(tenantId), boolQuery._toQuery());
        } catch (Exception ex) {
            logger.error("Error fetching count of cash drawers by assigned user", ex);
            throw new ReportGenerationException(ReportName.RETURNED_ITEMS.toString(), tenantId);
        }

        SearchRequest req = SearchRequest.of(s -> s
                .index(CashDrawer.OpenSearch.getIndex(tenantId))
                .query(boolQuery._toQuery())
                .from(0)
                .size(reportSize));

        SearchResponse<JsonNode> results;
        try {
            results = openSearchClient.search(req, JsonNode.class);
        } catch (Exception ex) {
            logger.error("Error fetching cash drawers by assigned user: ", ex);
            throw new ReportGenerationException(ReportName.RETURNED_ITEMS.toString(), tenantId);
        }

        return convertSearchResultsToModels(results, CashDrawer.class);
    }
}

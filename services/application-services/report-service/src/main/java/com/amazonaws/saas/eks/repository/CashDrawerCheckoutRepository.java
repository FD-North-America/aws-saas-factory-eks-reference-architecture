package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.cashdrawer.model.CashDrawer;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerCheckout;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class CashDrawerCheckoutRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(CashDrawerCheckoutRepository.class);

    public CashDrawer getCashDrawerByNumber(String tenantId, String number) {
        List<Query> mustQueries = new ArrayList<>();

        MatchQuery partitionKeyQuery = QueryBuilders.match()
                .field(CashDrawer.OpenSearch.FieldNames.PARTITION_KEY)
                .query(FieldValue.of(CashDrawer.OpenSearch.ENTITY))
                .build();
        mustQueries.add(partitionKeyQuery._toQuery());

        MatchQuery numberQuery = QueryBuilders.match()
                .field(CashDrawer.OpenSearch.FieldNames.NUMBER)
                .query(FieldValue.of(number))
                .build();
        mustQueries.add(numberQuery._toQuery());

        MatchQuery statusQuery = QueryBuilders.match()
                .field(CashDrawer.OpenSearch.FieldNames.STATUS)
                .query(FieldValue.of(CashDrawerStatus.DELETED.toString()))
                .build();

        BoolQuery boolQuery = QueryBuilders.bool()
                .must(mustQueries)
                .mustNot(statusQuery._toQuery())
                .build();

        SearchRequest req = SearchRequest.of(s -> s
                .index(CashDrawer.OpenSearch.getIndex(tenantId))
                .query(boolQuery._toQuery())
                .from(0)
                .size(1));

        SearchResponse<JsonNode> results;
        try {
            results = openSearchClient.search(req, JsonNode.class);
        } catch (Exception ex) {
            logger.error("Error fetching cash drawer by number: ", ex);
            throw new ReportGenerationException(ReportName.CASH_DRAWER_CHECKOUT.toString(), tenantId);
        }

        List<CashDrawer> cashDrawers = convertSearchResultsToModels(results, CashDrawer.class);
        return cashDrawers.isEmpty() ? null : cashDrawers.get(0);
    }

    public CashDrawerCheckout getCashDrawerCheckout(String tenantId, String cashDrawerId, String checkoutDate) {
        List<Query> mustQueries = new ArrayList<>();

        MatchQuery partitionKeyQuery = QueryBuilders.match()
                .field(CashDrawerCheckout.OpenSearch.FieldNames.PARTITION_KEY)
                .query(FieldValue.of(CashDrawerCheckout.OpenSearch.ENTITY))
                .build();
        mustQueries.add(partitionKeyQuery._toQuery());

        MatchQuery cashDrawerIdQuery = QueryBuilders.match()
                .field(CashDrawerCheckout.OpenSearch.FieldNames.CASH_DRAWER_ID)
                .query(FieldValue.of(cashDrawerId))
                .build();
        mustQueries.add(cashDrawerIdQuery._toQuery());

        RangeQuery checkoutDateQuery = QueryBuilders.range()
                .field(CashDrawerCheckout.OpenSearch.FieldNames.CREATED)
                .gte(JsonData.of(checkoutDate))
                .lte(JsonData.of(checkoutDate))
                .build();
        mustQueries.add(checkoutDateQuery._toQuery());

        BoolQuery boolQuery = QueryBuilders.bool()
                .must(mustQueries)
                .build();

        int reportSize;
        try {
            reportSize = getOpenSearchItemCount(CashDrawerCheckout.OpenSearch.getIndex(tenantId), boolQuery._toQuery());
        } catch (Exception ex) {
            logger.error("Error fetching cash drawer checkouts count", ex);
            throw new ReportGenerationException(ReportName.CASH_DRAWER_CHECKOUT.toString(), tenantId);
        }

        SearchRequest req = SearchRequest.of(s -> s
                .index(CashDrawerCheckout.OpenSearch.getIndex(tenantId))
                .query(boolQuery._toQuery())
                .from(0)
                .size(reportSize));

        SearchResponse<JsonNode> results;
        try {
            results = openSearchClient.search(req, JsonNode.class);
        } catch (Exception ex) {
            logger.error("Error fetching cash drawer checkouts by cashDrawerId and date: ", ex);
            throw new ReportGenerationException(ReportName.CASH_DRAWER_CHECKOUT.toString(), tenantId);
        }

        List<CashDrawerCheckout> cashDrawerCheckouts = convertSearchResultsToModels(results, CashDrawerCheckout.class);
        return cashDrawerCheckouts.isEmpty() ? null : cashDrawerCheckouts.get(0);
    }

    public List<Order> getOrdersByCashDrawerId(String tenantId, String cashDrawerId, Date from, Date to) {
        List<Query> mustQueries = new ArrayList<>();

        MatchQuery partitionKeyQuery = QueryBuilders.match()
                .field(Order.OpenSearch.FieldNames.PARTITION_KEY)
                .query(FieldValue.of(Order.OpenSearch.ENTITY))
                .build();
        mustQueries.add(partitionKeyQuery._toQuery());

        MatchQuery cashDrawerIdQuery = QueryBuilders.match()
                .field(Order.OpenSearch.FieldNames.CASH_DRAWER_ID)
                .query(FieldValue.of(cashDrawerId))
                .build();
        mustQueries.add(cashDrawerIdQuery._toQuery());

        MatchQuery statusQuery = QueryBuilders.match()
                .field(Order.OpenSearch.FieldNames.STATUS)
                .query(FieldValue.of(OrderStatus.PAID.toString()))
                .build();
        mustQueries.add(statusQuery._toQuery());

        RangeQuery checkoutDateQuery = QueryBuilders.range()
                .field(Order.OpenSearch.FieldNames.CREATED)
                .gte(JsonData.of(from))
                .lte(JsonData.of(to))
                .build();
        mustQueries.add(checkoutDateQuery._toQuery());

        BoolQuery boolQuery = QueryBuilders.bool()
                .must(mustQueries)
                .build();

        int reportSize;
        try {
            reportSize = getOpenSearchItemCount(Order.OpenSearch.getIndex(tenantId), boolQuery._toQuery());
        } catch (Exception ex) {
            logger.error("Error fetching count of orders by cashDrawerId", ex);
            throw new ReportGenerationException(ReportName.CASH_DRAWER_CHECKOUT.toString(), tenantId);
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
            logger.error("Error fetching orders by cashDrawerId: ", ex);
            throw new ReportGenerationException(ReportName.CASH_DRAWER_CHECKOUT.toString(), tenantId);
        }

        return convertSearchResultsToModels(results, Order.class);
    }
}

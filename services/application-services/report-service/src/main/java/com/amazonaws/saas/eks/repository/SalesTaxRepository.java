package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.ReportGenerationException;
import com.amazonaws.saas.eks.model.OrderData;
import com.amazonaws.saas.eks.model.enums.ReportName;
import com.amazonaws.saas.eks.model.opensearch.AggregationKeys;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.order.model.enums.OrderStatus;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.Script;
import org.opensearch.client.opensearch._types.aggregations.Aggregation;
import org.opensearch.client.opensearch._types.aggregations.AggregationBuilders;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.*;

import static com.amazonaws.saas.eks.util.Utils.convertToQueryDate;

@Repository
public class SalesTaxRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(SalesTaxRepository.class);

    public OrderData getSalesTaxData(String tenantId, ZonedDateTime fromDate, ZonedDateTime toDate, int from, int size) {
        List<Query> mustQueries = new ArrayList<>();

        MatchQuery statusQuery = QueryBuilders.match()
                .field(Order.OpenSearch.FieldNames.STATUS)
                .query(FieldValue.of(OrderStatus.PAID.toString()))
                .build();
        mustQueries.add(statusQuery._toQuery());

        RangeQuery dateQuery = QueryBuilders.range()
                .field(Order.OpenSearch.FieldNames.PAID_DATE)
                .gte(JsonData.of(convertToQueryDate(fromDate)))
                .lte(JsonData.of(convertToQueryDate(toDate)))
                .build();
        mustQueries.add(dateQuery._toQuery());

        BoolQuery boolQuery = QueryBuilders.bool()
                .must(mustQueries)
                .build();

        Map<String, Aggregation> aggregationMap = getAggregationMap();

        int reportSize;
        if (size == 0) {
            try {
                reportSize = getOpenSearchItemCount(Order.OpenSearch.getIndex(tenantId), boolQuery._toQuery());
            } catch (Exception ex) {
                logger.error("Error fetching data size for sales tax report: ", ex);
                throw new ReportGenerationException(ReportName.SALES_TAX.toString(), tenantId);
            }
        } else {
            reportSize = size;
        }

        SearchRequest req = SearchRequest.of(s -> s
                .index(Order.OpenSearch.getIndex(tenantId))
                .query(boolQuery._toQuery())
                .aggregations(aggregationMap)
                .from(from)
                .size(reportSize));

        SearchResponse<JsonNode> results;
        try {
            results = openSearchClient.search(req, JsonNode.class);
        } catch (Exception ex) {
            logger.error("Error fetching orders for sales tax report: ", ex);
            throw new ReportGenerationException(ReportName.SALES_TAX.toString(), tenantId);
        }

        List<Order> orders = convertSearchResultsToModels(results, Order.class);
        OrderData data = new OrderData();
        data.setOrders(orders);
        data.setCount(results.hits().total().value());
        for (String aggKey : results.aggregations().keySet()) {
            BigDecimal value = BigDecimal.valueOf(results.aggregations().get(aggKey).sum().value());
            data.getAggregationMap().put(aggKey, value.setScale(ROUND_SETTING, RoundingMode.HALF_UP));
        }
        return data;
    }

    private static Map<String, Aggregation> getAggregationMap() {
        Map<String, Aggregation> aggregationMap = new HashMap<>();

        String subTotalScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.SUBTOTAL_KEYWORD);
        Aggregation totalAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(subTotalScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_SUB_TOTAL, totalAgg);

        String taxScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.TAXABLE_SUB_TOTAL_KEYWORD);
        Aggregation taxTotalAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(taxScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_TAXABLE_SUBTOTAL, taxTotalAgg);

        String nonTotalScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.NON_TAXABLE_SUB_TOTAL_KEYWORD);
        Aggregation nonTaxTotalAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(nonTotalScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_NON_TAXABLE_TOTAL, nonTaxTotalAgg);

        String taxDueScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.TAX_TOTAL_KEYWORD);
        Aggregation taxDueAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(taxDueScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_TAX_DUE, taxDueAgg);

        return aggregationMap;
    }
}

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
import org.opensearch.client.opensearch._types.*;
import org.opensearch.client.opensearch._types.aggregations.Aggregate;
import org.opensearch.client.opensearch._types.aggregations.Aggregation;
import org.opensearch.client.opensearch._types.aggregations.AggregationBuilders;
import org.opensearch.client.opensearch._types.aggregations.TermsAggregation;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.*;

import static com.amazonaws.saas.eks.util.Utils.convertToQueryDate;

@Repository
public class SalesRegisterRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(SalesRegisterRepository.class);

    public OrderData getSalesRegisterData(String tenantId,
                                          ZonedDateTime fromDate,
                                          ZonedDateTime toDate,
                                          String invoiceNumberFrom,
                                          String invoiceNumberTo,
                                          String salesRep,
                                          int from,
                                          int size) {
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

        if (StringUtils.hasLength(salesRep)) {
            MatchQuery salesRepQuery = QueryBuilders.match()
                    .field(Order.OpenSearch.FieldNames.SALES_REP)
                    .query(FieldValue.of(salesRep))
                    .build();
            mustQueries.add(salesRepQuery._toQuery());
        }

        if (StringUtils.hasLength(invoiceNumberFrom)) {
            RangeQuery.Builder invNumberQuery = QueryBuilders.range()
                    .field(Order.OpenSearch.FieldNames.NUMBER_KEYWORD)
                    .gte(JsonData.of(invoiceNumberFrom));
            if (StringUtils.hasLength(invoiceNumberTo)) {
                invNumberQuery.lte(JsonData.of(invoiceNumberTo));
            }
            mustQueries.add(invNumberQuery.build()._toQuery());
        }

        BoolQuery boolQuery = QueryBuilders.bool()
                .must(mustQueries)
                .build();

        Map<String, Aggregation> aggregationMap = getAggregationMap();

        List<SortOptions> sortOptions = new ArrayList<>();
        FieldSort dateSort = FieldSort.of(f -> f.field(Order.OpenSearch.FieldNames.PAID_DATE).order(SortOrder.Asc));
        sortOptions.add(SortOptions.of(s -> s.field(dateSort)));

        int reportSize;
        if (size == 0) {
            try {
                reportSize = getOpenSearchItemCount(Order.OpenSearch.getIndex(tenantId), boolQuery._toQuery());
            } catch (Exception ex) {
                logger.error("Error fetching data size for sales register report: ", ex);
                throw new ReportGenerationException(ReportName.SALES_REGISTER.toString(), tenantId);
            }
        } else {
            reportSize = size;
        }

        SearchRequest req = SearchRequest.of(s -> s
                .index(Order.OpenSearch.getIndex(tenantId))
                .query(boolQuery._toQuery())
                .aggregations(aggregationMap)
                .sort(sortOptions)
                .from(from)
                .size(reportSize));

        SearchResponse<JsonNode> results;
        try {
            results = openSearchClient.search(req, JsonNode.class);
        } catch (Exception ex) {
            logger.error("Error fetching orders for sales register report: ", ex);
            throw new ReportGenerationException(ReportName.SALES_REGISTER.toString(), tenantId);
        }

        List<Order> orders = convertSearchResultsToModels(results, Order.class);
        OrderData data = new OrderData();
        data.setOrders(orders);
        data.setCount(results.hits().total().value());
        for (String aggKey : results.aggregations().keySet()) {
            BigDecimal value;
            Aggregate aggregate = results.aggregations().get(aggKey);
            if (aggregate.isSum()) {
                value = BigDecimal.valueOf(aggregate.sum().value());
                data.getAggregationMap().put(aggKey, value.setScale(ROUND_SETTING, RoundingMode.UP));
            } else if (aggregate.isFilter()) {
                value = BigDecimal.valueOf(aggregate.filter().docCount());
                data.getAggregationMap().put(aggKey, value.setScale(ROUND_SETTING, RoundingMode.UP));
            }
        }
        return data;
    }

    private static Map<String, Aggregation> getAggregationMap() {
        Map<String, Aggregation> aggregationMap = new HashMap<>();

        String salesTotalScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.SUBTOTAL_KEYWORD);
        Aggregation salesAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(salesTotalScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_SALES_TOTAL, salesAgg);

        String taxTotalScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.TAX_TOTAL_KEYWORD);
        Aggregation taxTotalAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(taxTotalScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_TAX_TOTAL, taxTotalAgg);

        String invTotalScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.TOTAL_KEYWORD);
        Aggregation invTotalAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(invTotalScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_INVOICE_TOTAL, invTotalAgg);

        String cardTransactionScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.CARD_TRANSACTION_COUNT_KEYWORD);
        Aggregation cardTransactionAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(cardTransactionScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_CARD_TRANSACTION_COUNT, cardTransactionAgg);

        String cashTransactionScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.CASH_TRANSACTION_COUNT_KEYWORD);
        Aggregation cashTransactionAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(cashTransactionScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_CASH_TRANSACTION_COUNT, cashTransactionAgg);

        String profitScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.PROFIT_KEYWORD);
        Aggregation profitAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(profitScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_PROFIT_TOTAL, profitAgg);

        String marginScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.MARGIN_KEYWORD);
        Aggregation marginAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(marginScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_MARGIN_TOTAL, marginAgg);

        Aggregation discountSubAgg = new Aggregation
                .Builder()
                .terms(new TermsAggregation
                        .Builder()
                        .field(Order.OpenSearch.FieldNames.DISCOUNT_TOTAL_KEYWORD)
                        .build())
                .build();
        // Only want to count the discounts where total does not equal 0
        Aggregation discountAgg = new Aggregation
                .Builder()
                .filter(QueryBuilders
                        .bool()
                        .mustNot(QueryBuilders
                                .match()
                                .field(Order.OpenSearch.FieldNames.DISCOUNT_TOTAL_KEYWORD)
                                .query(FieldValue.of("0"))
                                .build()._toQuery())
                        .build()._toQuery()).aggregations(new HashMap<>(){{
                            put("discount_count", discountSubAgg);
                }}).build();
        aggregationMap.put(AggregationKeys.SUM_DISCOUNT_TOTAL, discountAgg);

        String paidOutScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.PAID_OUT_TOTAL_KEYWORD);
        Aggregation paidOutAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(paidOutScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_PAID_OUT_TOTAL, paidOutAgg);

        String nonTaxableScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.NON_TAXABLE_SUB_TOTAL_KEYWORD);
        Aggregation nonTaxableAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(nonTaxableScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_NON_TAXABLE_TOTAL, nonTaxableAgg);

        String taxableScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.TAXABLE_SUB_TOTAL_KEYWORD);
        Aggregation taxableAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(taxableScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_TAXABLE_SUBTOTAL, taxableAgg);

        String subTotalScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.SUBTOTAL_KEYWORD);
        Aggregation totalAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(subTotalScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_SUB_TOTAL, totalAgg);

        String returnNonTaxableSubTotalScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.RETURN_NON_TAXABLE_SUB_TOTAL_KEYWORD);
        Aggregation returnNonTaxableAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(returnNonTaxableSubTotalScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_RETURN_NON_TAXABLE_SUB_TOTAL, returnNonTaxableAgg);

        String returnTaxableSubTotalScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.RETURN_TAXABLE_SUB_TOTAL_KEYWORD);
        Aggregation returnTaxableAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(returnTaxableSubTotalScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_RETURN_TAXABLE_SUB_TOTAL, returnTaxableAgg);

        String returnTotalScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.RETURN_TOTAL_KEYWORD);
        Aggregation returnTotalAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(returnTotalScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_RETURN_TOTAL, returnTotalAgg);

        String returnTaxTotalScript = String.format(AGGREGATION_SCRIPT, Order.OpenSearch.FieldNames.RETURN_TAX_TOTAL_KEYWORD);
        Aggregation returnTaxTotalAgg = AggregationBuilders
                .sum()
                .script(Script.of(s -> s.inline(i -> i.source(returnTaxTotalScript))))
                .build()
                ._toAggregation();
        aggregationMap.put(AggregationKeys.SUM_RETURN_TAX_TOTAL, returnTaxTotalAgg);

        return aggregationMap;
    }
}

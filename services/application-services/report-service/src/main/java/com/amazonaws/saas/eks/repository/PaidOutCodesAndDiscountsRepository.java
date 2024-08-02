package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.ReportGenerationException;
import com.amazonaws.saas.eks.model.enums.ReportName;
import com.amazonaws.saas.eks.order.model.Discount;
import com.amazonaws.saas.eks.order.model.PaidOutCode;
import com.amazonaws.saas.eks.order.model.enums.PaidOutCodeType;
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
public class PaidOutCodesAndDiscountsRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(PaidOutCodesAndDiscountsRepository.class);

    public List<PaidOutCode> getPaidOutCodes(String tenantId,
                                             ZonedDateTime fromDate,
                                             ZonedDateTime toDate,
                                             PaidOutCodeType type) {
        List<Query> mustQueries = new ArrayList<>();

        MatchQuery partitionKeyQuery = QueryBuilders.match()
                .field(PaidOutCode.OpenSearch.FieldNames.PARTITION_KEY)
                .query(FieldValue.of(PaidOutCode.OpenSearch.ENTITY))
                .build();
        mustQueries.add(partitionKeyQuery._toQuery());

        MatchQuery typeQuery = QueryBuilders.match()
                .field(PaidOutCode.OpenSearch.FieldNames.TYPE)
                .query(FieldValue.of(type.toString()))
                .build();
        mustQueries.add(typeQuery._toQuery());

        RangeQuery creationDateQuery = QueryBuilders.range()
                .field(PaidOutCode.OpenSearch.FieldNames.CREATED)
                .gte(JsonData.of(convertToQueryDate(fromDate)))
                .lte(JsonData.of(convertToQueryDate(toDate)))
                .build();
        mustQueries.add(creationDateQuery._toQuery());

        BoolQuery boolQuery = QueryBuilders.bool()
                .must(mustQueries)
                .build();

        int reportSize;
        try {
            reportSize = getOpenSearchItemCount(PaidOutCode.OpenSearch.getIndex(tenantId), boolQuery._toQuery());
        } catch (Exception ex) {
            logger.error("Error fetching count of paid out codes", ex);
            throw new ReportGenerationException(ReportName.PAID_OUT_CODES_AND_DISCOUNTS.toString(), tenantId);
        }

        SearchRequest req = SearchRequest.of(s -> s
                .index(PaidOutCode.OpenSearch.getIndex(tenantId))
                .query(boolQuery._toQuery())
                .from(0)
                .size(reportSize));

        SearchResponse<JsonNode> results;
        try {
            results = openSearchClient.search(req, JsonNode.class);
        } catch (Exception ex) {
            logger.error("Error fetching paid out codes: ", ex);
            throw new ReportGenerationException(ReportName.PAID_OUT_CODES_AND_DISCOUNTS.toString(), tenantId);
        }

        return convertSearchResultsToModels(results, PaidOutCode.class);
    }

    public List<Discount> getDiscounts(String tenantId, ZonedDateTime fromDate, ZonedDateTime toDate) {
        List<Query> mustQueries = new ArrayList<>();

        MatchQuery partitionKeyQuery = QueryBuilders.match()
                .field(Discount.OpenSearch.FieldNames.PARTITION_KEY)
                .query(FieldValue.of(Discount.OpenSearch.ENTITY))
                .build();
        mustQueries.add(partitionKeyQuery._toQuery());

        RangeQuery createdDateQuery = QueryBuilders.range()
                .field(Discount.OpenSearch.FieldNames.CREATED)
                .gte(JsonData.of(convertToQueryDate(fromDate)))
                .lte(JsonData.of(convertToQueryDate(toDate)))
                .build();
        mustQueries.add(createdDateQuery._toQuery());

        BoolQuery boolQuery = QueryBuilders.bool()
                .must(mustQueries)
                .build();

        int reportSize;
        try {
            reportSize = getOpenSearchItemCount(Discount.OpenSearch.getIndex(tenantId), boolQuery._toQuery());
        } catch (Exception ex) {
            logger.error("Error fetching count of discounts", ex);
            throw new ReportGenerationException(ReportName.PAID_OUT_CODES_AND_DISCOUNTS.toString(), tenantId);
        }

        SearchRequest req = SearchRequest.of(s -> s
                .index(Discount.OpenSearch.getIndex(tenantId))
                .query(boolQuery._toQuery())
                .from(0)
                .size(reportSize));

        SearchResponse<JsonNode> results;
        try {
            results = openSearchClient.search(req, JsonNode.class);
        } catch (Exception ex) {
            logger.error("Error fetching discounts: ", ex);
            throw new ReportGenerationException(ReportName.PAID_OUT_CODES_AND_DISCOUNTS.toString(), tenantId);
        }

        return convertSearchResultsToModels(results, Discount.class);
    }
}

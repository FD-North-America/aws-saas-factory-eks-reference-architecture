package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.SearchException;
import com.amazonaws.saas.eks.product.model.enums.EntityType;
import com.amazonaws.saas.eks.product.model.enums.VendorStatus;
import com.amazonaws.saas.eks.product.model.vendor.Vendor;
import com.amazonaws.saas.eks.product.model.vendor.VendorSearchResponse;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.opensearch._types.*;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchBoolPrefixQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.TermQuery;
import org.opensearch.client.opensearch.core.CountRequest;
import org.opensearch.client.opensearch.core.CountResponse;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Repository
public class VendorRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(VendorRepository.class);
    private static final String PARTITION_KEY_PLACEHOLDER = ":partitionKey";
    private static final String NUMBER_PLACEHOLDER = ":number";

    public Optional<Vendor> get(String tenantId, String vendorId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        return Optional.ofNullable(mapper.load(Vendor.class, Vendor.buildPartitionKey(tenantId), vendorId));
    }

    public Vendor save(String tenantId, Vendor vendor) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        if (vendor.getPartitionKey() == null) {
            vendor.setPartitionKey(Vendor.buildPartitionKey(tenantId));
        }
        if (vendor.getId() == null) {
            vendor.setId(String.valueOf(UUID.randomUUID()));
        }
        if (!StringUtils.hasLength(vendor.getNumber())) {
            vendor.setNumber(String.valueOf(getLatestCounter(tenantId, EntityType.VENDORS)));
        }
        if (vendor.getCreated() == null) {
            vendor.setCreated(new Date());
        }
        if (vendor.getModified() == null) {
            vendor.setModified(vendor.getCreated());
        } else {
            vendor.setModified(new Date());
        }
        if (!StringUtils.hasLength(vendor.getStatus())) {
            vendor.setStatus(VendorStatus.ACTIVE.toString());
        }

        mapper.save(vendor);
        return vendor;
    }

    public void delete(String tenantId, String id) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        get(tenantId, id).ifPresent(mapper::delete);
    }

    public List<Vendor> getByNumber(String tenantId, String number) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        Map<String, String> ean = new HashMap<>();
        ean.put("#" + Vendor.DbAttrNames.NUMBER, Vendor.DbAttrNames.NUMBER);
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(PARTITION_KEY_PLACEHOLDER, new AttributeValue().withS(Vendor.buildPartitionKey(tenantId)));
        eav.put(NUMBER_PLACEHOLDER, new AttributeValue().withS(number));
        DynamoDBQueryExpression<Vendor> query = new DynamoDBQueryExpression<Vendor>()
                .withIndexName(Vendor.DbIndexNames.NUMBER_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression(String.format("%s = %s and #%s = %s",
                        Vendor.DbAttrNames.PARTITION_KEY, PARTITION_KEY_PLACEHOLDER,
                        Vendor.DbAttrNames.NUMBER, NUMBER_PLACEHOLDER))
                .withExpressionAttributeValues(eav)
                .withExpressionAttributeNames(ean);

        return mapper.query(Vendor.class, query);
    }

    public VendorSearchResponse findAll(String tenantId,
                                        int from,
                                        int size,
                                        String filter,
                                        String sortBy) {

        List<Query> filterQueries = new ArrayList<>();
        TermQuery partitionKeyQuery = new TermQuery.Builder()
                .field(Vendor.OpenSearch.FieldNames.PARTITION_KEY_KEYWORD)
                .value(FieldValue.of(Vendor.buildPartitionKey(tenantId)))
                .build();
        filterQueries.add(partitionKeyQuery._toQuery());

        List<Query> searchQueries = new ArrayList<>();
        int minShouldMatch = 0;
        if (StringUtils.hasLength(filter)) {
            MatchBoolPrefixQuery nameQuery = new MatchBoolPrefixQuery.Builder()
                    .field(Vendor.OpenSearch.FieldNames.NAME)
                    .query(filter)
                    .build();
            searchQueries.add(nameQuery._toQuery());

            MatchBoolPrefixQuery numberQuery = new MatchBoolPrefixQuery.Builder()
                    .field(Vendor.OpenSearch.FieldNames.NUMBER)
                    .query(filter)
                    .build();
            searchQueries.add(numberQuery._toQuery());

            MatchBoolPrefixQuery contactQuery = new MatchBoolPrefixQuery.Builder()
                    .field(Vendor.OpenSearch.FieldNames.PHONE1)
                    .query(filter)
                    .build();
            searchQueries.add(contactQuery._toQuery());

            minShouldMatch++;
        }

        List<SortOptions> sorting = new ArrayList<>();
        if (StringUtils.hasLength(sortBy)) {
            String sortField = getSortField(sortBy);
            if (StringUtils.hasLength(sortField)) {
                SortOrder order = sortBy.contains(Vendor.SortKeys.SORT_DESC) ? SortOrder.Desc : SortOrder.Asc;
                FieldSort fieldSort = FieldSort.of(f -> f.field(sortField).order(order));
                sorting.add(SortOptions.of(s -> s.field(fieldSort)));
            }
        }

        BoolQuery boolQuery = new BoolQuery.Builder()
                .filter(filterQueries)
                .should(searchQueries)
                .minimumShouldMatch(String.valueOf(minShouldMatch))
                .build();

        int querySize;
        if (size == 0) {
            try {
                querySize = getOpenSearchItemCount(tenantId, boolQuery._toQuery());
            } catch (Exception ex) {
                logger.error("Error fetching data size for vendors: ", ex);
                throw new SearchException("Error fetching data size for vendors");
            }
        } else {
            querySize = size;
        }

        SearchRequest req = new SearchRequest.Builder()
                .query(boolQuery._toQuery())
                .from(from)
                .size(querySize)
                .sort(sorting)
                .build();

        try {
            SearchResponse<JsonNode> results = openSearchClient.search(req, JsonNode.class);
            VendorSearchResponse response = new VendorSearchResponse();
            response.setVendors(convertSearchResultsToModels(results, Vendor.class, tenantId));
            response.setCount(results.hits().total().value());
            return response;
        } catch (IOException e) {
            logger.error("Error while searching for vendors", e);
            throw new SearchException("Error while searching for vendors");
        }
    }

    private String getSortField(String sortBy) {
        if (sortBy.toLowerCase().contains(Vendor.SortKeys.NUMBER)) {
            return Vendor.OpenSearch.FieldNames.NUMBER_KEYWORD;
        }
        if (sortBy.toLowerCase().contains(Vendor.SortKeys.NAME)) {
            return Vendor.OpenSearch.FieldNames.NAME_KEYWORD;
        }
        if (sortBy.toLowerCase().contains(Vendor.SortKeys.PHONE)) {
            return Vendor.OpenSearch.FieldNames.PHONE_NUMBER_KEYWORD;
        }
        if (sortBy.toLowerCase().contains(Vendor.SortKeys.ADDRESS)) {
            return Vendor.OpenSearch.FieldNames.ADDRESS_KEYWORD;
        }
        if (sortBy.toLowerCase().contains(Vendor.SortKeys.CITY)) {
            return Vendor.OpenSearch.FieldNames.CITY_KEYWORD;
        }
        if (sortBy.toLowerCase().contains(Vendor.SortKeys.STATE)) {
            return Vendor.OpenSearch.FieldNames.STATE_KEYWORD;
        }
        if (sortBy.toLowerCase().contains(Vendor.SortKeys.ZIP)) {
            return Vendor.OpenSearch.FieldNames.ZIP_KEYWORD;
        }
        if (sortBy.toLowerCase().contains(Vendor.SortKeys.STATUS)) {
            return Vendor.OpenSearch.FieldNames.STATUS_KEYWORD;
        }
        return "";
    }

    private int getOpenSearchItemCount(String tenantId, Query query) throws IOException, OpenSearchException {
        CountRequest countRequest = CountRequest.of(c -> c
                .index(Vendor.OpenSearch.getIndex(tenantId))
                .query(query)
        );
        CountResponse countResponse = openSearchClient.count(countRequest);
        return (int) countResponse.count();
    }
}

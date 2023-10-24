package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerCheckout;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerCheckoutSearchResponse;
import com.amazonaws.saas.eks.cashdrawer.model.enums.CashDrawerStatus;
import com.amazonaws.saas.eks.cashdrawer.model.enums.EntityType;
import com.amazonaws.saas.eks.exception.CashDrawerCheckoutNotFoundException;
import com.amazonaws.saas.eks.exception.CashDrawerException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldSort;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.CountRequest;
import org.opensearch.client.opensearch.core.CountResponse;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Repository
public class CashDrawerCheckoutRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(CashDrawerCheckoutRepository.class);

    @Autowired
    private OpenSearchClient openSearchClient;

    public CashDrawerCheckout create(CashDrawerCheckout cashDrawerCheckout, String tenantId) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);

            cashDrawerCheckout.setPartitionKey(EntityType.CASHDRAWERCHECKOUTS.getLabel());
            cashDrawerCheckout.setId(String.valueOf(UUID.randomUUID()));
            cashDrawerCheckout.setCreated(new Date());
            cashDrawerCheckout.setModified(cashDrawerCheckout.getCreated());
            cashDrawerCheckout.setStatus(CashDrawerStatus.CHECKED.toString());

            mapper.save(cashDrawerCheckout);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Create Cash Drawer Checkout failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new CashDrawerException(message);
        }

        return cashDrawerCheckout;
    }

    public CashDrawerCheckout getByCashDrawerId(String cashDrawerId, String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        Map<String, AttributeValue> eav = new HashMap<>();
        Map<String, String> ean = new HashMap<>();
        ean.put("#" + CashDrawerCheckout.DbAttrNames.STATUS, CashDrawerCheckout.DbAttrNames.STATUS); // create alias for reserved word "Status"
        eav.put(":partitionKey", new AttributeValue().withS(EntityType.CASHDRAWERCHECKOUTS.getLabel()));
        eav.put(":cashDrawerId", new AttributeValue().withS(cashDrawerId));
        eav.put(":status", new AttributeValue().withS(CashDrawerStatus.CHECKED.toString()));
        DynamoDBQueryExpression<CashDrawerCheckout> query = new DynamoDBQueryExpression<CashDrawerCheckout>()
                .withIndexName(CashDrawerCheckout.DbIndexNames.CASH_DRAWER_ID_INDEX)
                .withConsistentRead(false)
                .withFilterExpression(String.format("#%s = :status", CashDrawerCheckout.DbAttrNames.STATUS))
                .withKeyConditionExpression(String.format("%s = :partitionKey AND %s = :cashDrawerId",
                        CashDrawerCheckout.DbAttrNames.PARTITION_KEY, CashDrawerCheckout.DbAttrNames.CASH_DRAWER_ID))
                .withExpressionAttributeValues(eav)
                .withExpressionAttributeNames(ean);
        List<CashDrawerCheckout> results = mapper.query(CashDrawerCheckout.class, query);
        if (results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }

    public CashDrawerCheckout getById(String tenantId, String checkoutId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        CashDrawerCheckout checkout = mapper.load(CashDrawerCheckout.class, EntityType.CASHDRAWERCHECKOUTS.getLabel(), checkoutId);
        if (checkout == null) {
            throw new CashDrawerCheckoutNotFoundException(checkoutId, tenantId);
        }
        return checkout;
    }

    public CashDrawerCheckoutSearchResponse get(String tenantId, String username) {
        List<Query> mustNotQueries = new ArrayList<>();
        MatchQuery statusQuery = new MatchQuery.Builder()
                .field(CashDrawerCheckout.OpenSearch.FieldNames.STATUS)
                .query(FieldValue.of(CashDrawerStatus.DELETED.toString()))
                .build();
        mustNotQueries.add(statusQuery._toQuery());

        List<Query> matchQueries = new ArrayList<>();
        if (StringUtils.hasLength(username)) {
            MatchQuery userQuery = new MatchQuery.Builder()
                    .field(CashDrawerCheckout.OpenSearch.FieldNames.CHECKOUT_REP)
                    .query(FieldValue.of(username))
                    .build();
            matchQueries.add(userQuery._toQuery());
        }

        List<SortOptions> sorting = new ArrayList<>();
        FieldSort fieldSort = FieldSort.of(f -> f.field(CashDrawerCheckout.OpenSearch.FieldNames.SORT_CREATED).order(SortOrder.Desc));
        sorting.add(SortOptions.of(s -> s.field(fieldSort)));

        BoolQuery boolQuery = new BoolQuery.Builder()
                .mustNot(mustNotQueries)
                .must(matchQueries)
                .build();

        String index = CashDrawerCheckout.OpenSearch.getIndex(tenantId);


        try {
            CashDrawerCheckoutSearchResponse response = new CashDrawerCheckoutSearchResponse();

            CountRequest countRequest = CountRequest.of(c -> c.index(index).query(boolQuery._toQuery()));
            CountResponse countResponse = openSearchClient.count(countRequest);
            if (countResponse.count() == 0) {
                response.setCheckouts(new ArrayList<>());
                return response;
            }

            SearchRequest req = SearchRequest.of(s -> s
                    .index(index)
                    .query(boolQuery._toQuery())
                    .from(0)
                    .size((int) countResponse.count())
                    .sort(sorting));
            SearchResponse<JsonNode> results = openSearchClient.search(req, JsonNode.class);

            response.setCheckouts(convertSearchResultsToModels(dynamoDBMapper(tenantId), results, CashDrawerCheckout.class));
            response.setCount(results.hits().total().value());
            return response;
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Error reading from OpenSearch: %s", tenantId, e);
            logger.error(message);
            throw new CashDrawerException(message);
        }
    }

    public CashDrawerCheckout update(CashDrawerCheckout cashDrawerCheckout, String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        cashDrawerCheckout.setModified(new Date());
        mapper.save(cashDrawerCheckout);
        return cashDrawerCheckout;
    }
}

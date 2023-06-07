package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.CashDrawerException;
import com.amazonaws.saas.eks.model.CashDrawer;
import com.amazonaws.saas.eks.model.CashDrawerCheckout;
import com.amazonaws.saas.eks.model.CashDrawerSearchResponse;
import com.amazonaws.saas.eks.model.DynamoDbStreamRecord;
import com.amazonaws.saas.eks.model.enums.CashDrawerStatus;
import com.amazonaws.saas.eks.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldSort;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchBoolPrefixQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CashDrawerRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(CashDrawerRepository.class);

    private static final String OPEN_SEARCH_INDEX = "cash-drawers-index";
    private static final String OPEN_SEARCH_STATUS = "NewImage.Status.S";
    private static final String OPEN_SEARCH_NUMBER = "NewImage.Number.S";
    private static final String OPEN_SEARCH_DESCRIPTION = "NewImage.Description.S";
    private static final String OPEN_SEARCH_ASSIGNED_USER = "NewImage.AssignedUser.S";
    private static final String OPEN_SEARCH_SORT_NUMBER = "NewImage.Number.S.keyword";
    private static final String OPEN_SEARCH_SORT_DESCRIPTION = "NewImage.Description.S.keyword";
    private static final String OPEN_SEARCH_SORT_STATUS = "NewImage.Status.S.keyword";
    private static final String OPEN_SEARCH_SORT_USER = "NewImage.AssignedUser.S.keyword";

    private static final String SORT_BY_NUMBER = "number";
    private static final String SORT_BY_DESCRIPTION = "description";
    private static final String SORT_BY_STATUS = "status";
    private static final String SORT_BY_USER = "user";
    private static final String SORT_BY_DESC = "desc";

    @Autowired
    private OpenSearchClient openSearchClient;

    public CashDrawer create(CashDrawer cashDrawer, String tenantId) {
        CashDrawer model = getByNumber(tenantId, cashDrawer.getNumber());
        if (model != null) {
            String message = String.format("Already exists a cash drawer with number %s. Id: %s. TenantId: %s",
                    cashDrawer.getNumber(), model.getId(), tenantId);
            logger.error(message);
            throw new CashDrawerException(message);
        }

        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            if (!StringUtils.hasLength(cashDrawer.getPartitionKey())) {
                cashDrawer.setPartitionKey(EntityType.CASHDRAWERS.getLabel());
            }
            if (!StringUtils.hasLength(cashDrawer.getId())) {
                cashDrawer.setId(String.valueOf(UUID.randomUUID()));
            }
            if (cashDrawer.getCreated() == null) {
                cashDrawer.setCreated(new Date());
            }
            if (cashDrawer.getModified() == null) {
                cashDrawer.setModified(cashDrawer.getCreated());
            }
            if (cashDrawer.getAutoStartup() == null) {
                cashDrawer.setAutoStartup(false);
            }
            if (cashDrawer.getStartUpAmount() == null) {
                cashDrawer.setStartUpAmount(BigDecimal.ZERO);
            }

            cashDrawer.setStatus(CashDrawerStatus.ACTIVE.toString());
            cashDrawer.setStartupDate(new Date());
            mapper.save(cashDrawer);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Create Cash Drawer failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new CashDrawerException(message);
        }

        return cashDrawer;
    }

    public CashDrawer update(CashDrawer cashDrawer, String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        CashDrawer model = get(cashDrawer.getId(), tenantId);

        if (model.getStatus().equals(CashDrawerStatus.DELETED.toString())) {
            String message = "Cannot update a cash drawer that's been marked as deleted";
            logger.error(message);
            throw new CashDrawerException(message);
        }

        try {
            if (StringUtils.hasLength(cashDrawer.getDescription())) {
                model.setDescription(cashDrawer.getDescription());
            }
            String status =  cashDrawer.getStatus();
            if (StringUtils.hasLength(status) && !status.equals(CashDrawerStatus.DELETED.toString())) {
                if(!model.getStatus().equals(status) && status.equals(CashDrawerStatus.ACTIVE.toString())){
                    model.setStartupDate(new Date());
                }
                model.setStatus(status);
            }
            if (cashDrawer.getAutoStartup() != null) {
                model.setAutoStartup(cashDrawer.getAutoStartup());
            }
            if (cashDrawer.getStartUpAmount() != null) {
                model.setStartUpAmount(cashDrawer.getStartUpAmount());
            }
            model.setAssignedUser(cashDrawer.getAssignedUser());
            if (StringUtils.hasLength(cashDrawer.getStartupRep())) {
                model.setStartupRep(cashDrawer.getStartupRep());
            }
            if (StringUtils.hasLength(cashDrawer.getCheckoutRep())) {
                model.setCheckoutRep(cashDrawer.getCheckoutRep());
            }
            if (cashDrawer.getTrays() != null) {
                model.setTrays(cashDrawer.getTrays());
            }

            model.setModified(new Date());

            mapper.save(model);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Update Cash Drawer failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new CashDrawerException(message);
        }

        return model;
    }

    public CashDrawer get(String cashDrawerId, String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        CashDrawer cashDrawer;
        try {
            cashDrawer = mapper.load(CashDrawer.class, EntityType.CASHDRAWERS.getLabel(), cashDrawerId);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Get Cash Drawer failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new CashDrawerException(message);
        }

        return cashDrawer;
    }

    public CashDrawer getByNumber(String tenantId, String number) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        Map<String, String> ean = new HashMap<>();
        ean.put("#" + CashDrawer.STATUS, CashDrawer.STATUS); // create alias for reserved word "Status"
        ean.put("#" + CashDrawer.NUMBER, CashDrawer.NUMBER); // create alias for reserved word "Number"
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":partitionKey", new AttributeValue().withS(EntityType.CASHDRAWERS.getLabel()));
        eav.put(":number", new AttributeValue().withS(number));
        eav.put(":status", new AttributeValue().withS(CashDrawerStatus.DELETED.toString()));
        DynamoDBQueryExpression<CashDrawer> query = new DynamoDBQueryExpression<CashDrawer>()
                .withIndexName(CashDrawer.CASH_DRAWER_NUMBER_INDEX)
                .withConsistentRead(false)
                .withFilterExpression(String.format("#%s <> :status", CashDrawerCheckout.STATUS))
                .withKeyConditionExpression(String.format("%s = :partitionKey AND #%s = :number",
                        CashDrawer.PARTITION_KEY, CashDrawer.NUMBER))
                .withExpressionAttributeValues(eav)
                .withExpressionAttributeNames(ean);
        List<CashDrawer> results = mapper.query(CashDrawer.class, query);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    public void delete(String cashDrawerId, String tenantId) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            CashDrawer cashDrawer = get(cashDrawerId, tenantId);
            cashDrawer.setStatus(CashDrawerStatus.DELETED.toString());
            cashDrawer.setModified(new Date());
            mapper.save(cashDrawer);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Delete Cash Drawer failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new CashDrawerException(message);
        }
    }

    public CashDrawerSearchResponse search(String tenantId,
                                           int from,
                                           int size,
                                           String filter,
                                           String sortBy) {
        try {
            // Filtering out any Deleted Cash Drawers
            MatchQuery statusQuery = new MatchQuery.Builder().field(OPEN_SEARCH_STATUS)
                    .query(FieldValue.of(CashDrawerStatus.DELETED.toString())).build();
            List<Query> mustNotQueries = new ArrayList<>();
            mustNotQueries.add(statusQuery._toQuery());

            List<Query> searchQueries = new ArrayList<>();
            int minShouldMatch = 0;
            if (StringUtils.hasLength(filter)) {
                MatchBoolPrefixQuery numberQuery = new MatchBoolPrefixQuery.Builder().field(OPEN_SEARCH_NUMBER)
                        .query(filter).build();
                MatchBoolPrefixQuery descriptionQuery = new MatchBoolPrefixQuery.Builder().field(OPEN_SEARCH_DESCRIPTION)
                        .query(filter).build();
                MatchBoolPrefixQuery userQuery = new MatchBoolPrefixQuery.Builder().field(OPEN_SEARCH_ASSIGNED_USER)
                                .query(filter).build();
                searchQueries.add(numberQuery._toQuery());
                searchQueries.add(descriptionQuery._toQuery());
                searchQueries.add(userQuery._toQuery());
                minShouldMatch = 1;
            }

            List<SortOptions> sorting = new ArrayList<>();
            if (StringUtils.hasLength(sortBy)) {
                String field = getOpenSearchSortField(sortBy);
                if (StringUtils.hasLength(field)) {
                    SortOrder order = sortBy.contains(SORT_BY_DESC) ? SortOrder.Desc : SortOrder.Asc;
                    FieldSort fieldSort = FieldSort.of(f -> f.field(field).order(order));
                    sorting.add(SortOptions.of(s -> s.field(fieldSort)));
                }
            }

            BoolQuery boolQuery = new BoolQuery.Builder()
                    .mustNot(mustNotQueries)
                    .should(searchQueries)
                    .minimumShouldMatch(String.valueOf(minShouldMatch))
                    .build();

            String index = String.format("%s-%s", tenantId, OPEN_SEARCH_INDEX);
            SearchRequest req = SearchRequest.of(s -> s
                    .index(index)
                    .query(boolQuery._toQuery())
                    .sort(sorting)
                    .from(from)
                    .size(size));
            SearchResponse<JsonNode> results = openSearchClient.search(req, JsonNode.class);
            CashDrawerSearchResponse response = new CashDrawerSearchResponse();
            response.setCashDrawers(convertSearchResultsToCashDrawers(tenantId, results));
            response.setCount(results.hits().total().value());
            return response;
        } catch (IOException e) {
            logger.error("Error reading from OpenSearch: ", e);
            throw new CashDrawerException("Error fetching all CashDrawers");
        }
    }

    private List<CashDrawer> convertSearchResultsToCashDrawers(String tenantId, SearchResponse<JsonNode> results) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        List<DynamoDbStreamRecord> records = new ArrayList<>();
        for(int i = 0; i < results.hits().hits().size(); i++) {
            DynamoDbStreamRecord r = objectMapper.convertValue(results.hits().hits().get(i).source(), DynamoDbStreamRecord.class);
            records.add(r);
        }

        List<Map<String, AttributeValue>> dynamoDbProductAttributes = records
                .stream()
                .map(DynamoDbStreamRecord::getNewImage)
                .collect(Collectors.toList());
        return mapper.marshallIntoObjects(CashDrawer.class, dynamoDbProductAttributes);
    }

    private String getOpenSearchSortField(String sortBy) {
        if (sortBy.toLowerCase().contains(SORT_BY_NUMBER)) {
            return OPEN_SEARCH_SORT_NUMBER;
        }
        if (sortBy.toLowerCase().contains(SORT_BY_DESCRIPTION)) {
            return OPEN_SEARCH_SORT_DESCRIPTION;
        }
        if (sortBy.toLowerCase().contains(SORT_BY_STATUS)) {
            return OPEN_SEARCH_SORT_STATUS;
        }
        if (sortBy.toLowerCase().contains(SORT_BY_USER)) {
            return OPEN_SEARCH_SORT_USER;
        }

        return "";
    }
}

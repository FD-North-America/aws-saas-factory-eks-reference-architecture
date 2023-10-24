package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.cashdrawer.model.CashDrawer;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerSearchResponse;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerTray;
import com.amazonaws.saas.eks.cashdrawer.model.enums.CashDrawerCurrencyType;
import com.amazonaws.saas.eks.cashdrawer.model.enums.CashDrawerStatus;
import com.amazonaws.saas.eks.cashdrawer.model.enums.EntityType;
import com.amazonaws.saas.eks.exception.CashDrawerException;
import com.amazonaws.saas.eks.exception.CashDrawerNotFoundException;
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

@Repository
public class CashDrawerRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(CashDrawerRepository.class);

    private static final String SORT_BY_NUMBER = "number";
    private static final String SORT_BY_DESCRIPTION = "description";
    private static final String SORT_BY_STATUS = "status";
    private static final String SORT_BY_USER = "user";
    private static final String SORT_BY_DESC = "desc";
    private static final int DYNAMO_THRESHOLD = 100;

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

            cashDrawer.setPartitionKey(EntityType.CASHDRAWERS.getLabel());
            cashDrawer.setId(String.valueOf(UUID.randomUUID()));
            cashDrawer.setCreated(new Date());
            cashDrawer.setModified(cashDrawer.getCreated());
            if (cashDrawer.getAutoStartup() == null) {
                cashDrawer.setAutoStartup(false);
            }
            if (cashDrawer.getStartUpAmount() == null) {
                cashDrawer.setStartUpAmount(BigDecimal.ZERO);
            }
            cashDrawer.setStatus(CashDrawerStatus.ACTIVE.toString());
            cashDrawer.setStartupDate(new Date());

            if (!cashDrawer.getTrays().isEmpty()) {
                cashDrawer.setTraysTotalAmount(computeTraysTotalAmount(cashDrawer.getTrays()));
            }

            mapper.save(cashDrawer);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Create Cash Drawer failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new CashDrawerException(message);
        }

        return cashDrawer;
    }

    public CashDrawer get(String cashDrawerId, String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        CashDrawer cashDrawer = mapper.load(CashDrawer.class, EntityType.CASHDRAWERS.getLabel(), cashDrawerId);
        if (cashDrawer == null) {
            throw new CashDrawerNotFoundException(cashDrawerId, tenantId);
        }
        return cashDrawer;
    }

    public CashDrawer update(CashDrawer cashDrawer, String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        CashDrawer model = get(cashDrawer.getId(), tenantId);

        if (model.getStatus().equals(CashDrawerStatus.DELETED.toString())) {
            String message = String.format("TenantId: %s-Cannot update a cash drawer that's been marked as deleted", tenantId);
            logger.error(message);
            throw new CashDrawerException(message);
        }

        try {
            if (StringUtils.hasLength(cashDrawer.getDescription())) {
                model.setDescription(cashDrawer.getDescription());
            }
            String status =  cashDrawer.getStatus();
            if (StringUtils.hasLength(status) && !status.equals(CashDrawerStatus.DELETED.toString())) {
                if (!model.getStatus().equals(status) && status.equals(CashDrawerStatus.ACTIVE.toString())) {
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
            if (cashDrawer.getStartupDate() != null) {
                model.setStartupDate(cashDrawer.getStartupDate());
            }
            if (StringUtils.hasLength(cashDrawer.getStartupRep())) {
                model.setStartupRep(cashDrawer.getStartupRep());
            }
            if (cashDrawer.getCheckoutDate() != null) {
                model.setCheckoutDate(cashDrawer.getCheckoutDate());
            }
            if (StringUtils.hasLength(cashDrawer.getCheckoutRep())) {
                model.setCheckoutRep(cashDrawer.getCheckoutRep());
            }
            if (cashDrawer.getCheckoutAmounts() != null) {
                model.setCheckoutAmounts(cashDrawer.getCheckoutAmounts());
            }
            if (cashDrawer.getClearedDate() != null) {
                model.setClearedDate(cashDrawer.getClearedDate());
            }
            if (StringUtils.hasLength(cashDrawer.getClearedBy())) {
                model.setClearedBy(cashDrawer.getClearedBy());
            }
            if (cashDrawer.getTrays() != null) {
                model.setTrays(cashDrawer.getTrays());
                if (!cashDrawer.getTrays().isEmpty()) {
                    model.setTraysTotalAmount(computeTraysTotalAmount(cashDrawer.getTrays()));
                }
            }
            if (cashDrawer.getTraysTotalAmount() != null) {
                model.setTraysTotalAmount(cashDrawer.getTraysTotalAmount());
                model.setTrays(new ArrayList<>());
            }
            if (cashDrawer.getCashTotalAmount() != null) {
                model.setCashTotalAmount(cashDrawer.getCashTotalAmount());
            }
            if (cashDrawer.getCardTotalAmount() != null) {
                model.setCardTotalAmount(cashDrawer.getCardTotalAmount());
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

    public CashDrawer getByNumber(String tenantId, String number) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        Map<String, String> ean = new HashMap<>();
        ean.put("#" + CashDrawer.DbAttrNames.STATUS, CashDrawer.DbAttrNames.STATUS); // create alias for reserved word "Status"
        ean.put("#" + CashDrawer.DbAttrNames.NUMBER, CashDrawer.DbAttrNames.NUMBER); // create alias for reserved word "Number"
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":partitionKey", new AttributeValue().withS(EntityType.CASHDRAWERS.getLabel()));
        eav.put(":number", new AttributeValue().withS(number));
        eav.put(":status", new AttributeValue().withS(CashDrawerStatus.DELETED.toString()));
        DynamoDBQueryExpression<CashDrawer> query = new DynamoDBQueryExpression<CashDrawer>()
                .withIndexName(CashDrawer.DbIndexNames.CASH_DRAWER_NUMBER_INDEX)
                .withConsistentRead(false)
                .withFilterExpression(String.format("#%s <> :status", CashDrawer.DbAttrNames.STATUS))
                .withKeyConditionExpression(String.format("%s = :partitionKey AND #%s = :number",
                        CashDrawer.DbAttrNames.PARTITION_KEY, CashDrawer.DbAttrNames.NUMBER))
                .withExpressionAttributeValues(eav)
                .withExpressionAttributeNames(ean);
        List<CashDrawer> results = mapper.query(CashDrawer.class, query);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    public List<CashDrawer> getByAssignedUser(String tenantId, String username) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        Map<String, String> ean = new HashMap<>();
        ean.put("#" + CashDrawer.DbAttrNames.STATUS, CashDrawer.DbAttrNames.STATUS); // create alias for reserved word "Status";
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":partitionKey", new AttributeValue().withS(EntityType.CASHDRAWERS.getLabel()));
        eav.put(":status", new AttributeValue().withS(CashDrawerStatus.DELETED.toString()));
        eav.put(":assignedUser", new AttributeValue().withS(username));
        DynamoDBQueryExpression<CashDrawer> query = new DynamoDBQueryExpression<CashDrawer>()
                .withIndexName(CashDrawer.DbIndexNames.CASH_DRAWER_ASSIGNED_USER_INDEX)
                .withConsistentRead(false)
                .withFilterExpression(String.format("#%s <> :status", CashDrawer.DbAttrNames.STATUS))
                .withKeyConditionExpression(String.format("%s = :partitionKey AND %s = :assignedUser",
                        CashDrawer.DbAttrNames.PARTITION_KEY, CashDrawer.DbAttrNames.ASSIGNED_USER))
                .withExpressionAttributeValues(eav)
                .withExpressionAttributeNames(ean);
        return mapper.query(CashDrawer.class, query);
    }

    public CashDrawerSearchResponse search(String tenantId,
                                           int from,
                                           int size,
                                           String filter,
                                           String sortBy) {
        if (meetsDynamoCriteria(filter, from, size, sortBy)) {
            List<CashDrawer> cashDrawers = getFromDynamo(tenantId);
            CashDrawerSearchResponse response = new CashDrawerSearchResponse();
            response.setCashDrawers(cashDrawers);
            response.setCount(cashDrawers.size());
            return response;
        }

        List<Query> mustNotQueries = new ArrayList<>();
        MatchQuery statusQuery = new MatchQuery.Builder()
                .field(CashDrawer.OpenSearch.FieldNames.STATUS)
                .query(FieldValue.of(CashDrawerStatus.DELETED.toString()))
                .build();
        mustNotQueries.add(statusQuery._toQuery());

        List<Query> searchQueries = new ArrayList<>();
        int minShouldMatch = 0;
        if (StringUtils.hasLength(filter)) {
            MatchBoolPrefixQuery numberQuery = new MatchBoolPrefixQuery.Builder()
                    .field(CashDrawer.OpenSearch.FieldNames.NUMBER)
                    .query(filter)
                    .build();
            searchQueries.add(numberQuery._toQuery());

            MatchBoolPrefixQuery descriptionQuery = new MatchBoolPrefixQuery.Builder()
                    .field(CashDrawer.OpenSearch.FieldNames.DESCRIPTION)
                    .query(filter)
                    .build();
            searchQueries.add(descriptionQuery._toQuery());

            MatchBoolPrefixQuery userQuery = new MatchBoolPrefixQuery.Builder()
                    .field(CashDrawer.OpenSearch.FieldNames.ASSIGNED_USER)
                    .query(filter)
                    .build();
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

        String index = CashDrawer.OpenSearch.getIndex(tenantId);
        SearchRequest req = SearchRequest.of(s -> s
                .index(index)
                .query(boolQuery._toQuery())
                .sort(sorting)
                .from(from)
                .size(size));

        try {
            SearchResponse<JsonNode> results = openSearchClient.search(req, JsonNode.class);

            CashDrawerSearchResponse response = new CashDrawerSearchResponse();
            response.setCashDrawers(convertSearchResultsToModels(dynamoDBMapper(tenantId), results, CashDrawer.class));
            response.setCount(results.hits().total().value());
            return response;
        } catch (IOException e) {
            String message = String.format("TenantId: %s-Error reading from OpenSearch: %s", tenantId, e);
            logger.error(message);
            throw new CashDrawerException(message);
        }
    }

    private String getOpenSearchSortField(String sortBy) {
        if (sortBy.toLowerCase().contains(SORT_BY_NUMBER)) {
            return CashDrawer.OpenSearch.FieldNames.SORT_NUMBER;
        }
        if (sortBy.toLowerCase().contains(SORT_BY_DESCRIPTION)) {
            return CashDrawer.OpenSearch.FieldNames.SORT_DESCRIPTION;
        }
        if (sortBy.toLowerCase().contains(SORT_BY_STATUS)) {
            return CashDrawer.OpenSearch.FieldNames.SORT_STATUS;
        }
        if (sortBy.toLowerCase().contains(SORT_BY_USER)) {
            return CashDrawer.OpenSearch.FieldNames.SORT_ASSIGNED_USER;
        }

        return "";
    }

    private List<CashDrawer> getFromDynamo(String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        Map<String, String> ean = new HashMap<>();
        ean.put("#" + CashDrawer.DbAttrNames.STATUS, CashDrawer.DbAttrNames.STATUS); // create alias for reserved word "Status"
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":partitionKey", new AttributeValue().withS(EntityType.CASHDRAWERS.getLabel()));
        eav.put(":status", new AttributeValue().withS(CashDrawerStatus.DELETED.toString()));
        DynamoDBQueryExpression<CashDrawer> query = new DynamoDBQueryExpression<CashDrawer>()
                .withIndexName(CashDrawer.DbIndexNames.CASH_DRAWER_NUMBER_INDEX)
                .withConsistentRead(false)
                .withFilterExpression(String.format("#%s <> :status", CashDrawer.DbAttrNames.STATUS))
                .withKeyConditionExpression(String.format("%s = :partitionKey", CashDrawer.DbAttrNames.PARTITION_KEY))
                .withExpressionAttributeValues(eav)
                .withLimit(DYNAMO_THRESHOLD)
                .withExpressionAttributeNames(ean);
        return mapper.query(CashDrawer.class, query);
    }

    private BigDecimal computeTraysTotalAmount(List<CashDrawerTray> trays) {
        BigDecimal total = BigDecimal.ZERO;
        double currencyValue = 0.0;
        double rollValue = 0.0;
        for (CashDrawerTray tray: trays) {
            switch (CashDrawerCurrencyType.valueOfLabel(tray.getCurrency())) {
                case PENNY:
                    currencyValue = 0.01;
                    rollValue = 0.5;
                    break;
                case NICKEL:
                    currencyValue = 0.05;
                    rollValue = 2;
                    break;
                case DIME:
                    currencyValue = 0.10;
                    rollValue = 5;
                    break;
                case QUARTER:
                    currencyValue = 0.25;
                    rollValue = 10;
                    break;
                case DOLLAR:
                    currencyValue = 1.0;
                    rollValue = 25;
                    break;
                case TWO:
                    currencyValue = 2.0;
                    rollValue = 200;
                    break;
                case FIVE:
                    currencyValue = 5.0;
                    rollValue = 500;
                    break;
                case TEN:
                    currencyValue = 10.0;
                    rollValue = 1000;
                    break;
                case TWENTY:
                    currencyValue = 20.0;
                    rollValue = 2000;
                    break;
                case FIFTY:
                    currencyValue = 50.0;
                    rollValue = 5000;
                    break;
                case HUNDRED:
                    currencyValue = 100.0;
                    rollValue = 10000;
                    break;
            }
            total = total.add(
                    BigDecimal.valueOf(currencyValue)
                            .multiply(tray.getAmount())
                            .add(BigDecimal.valueOf(tray.getRolls() * rollValue))
            );
        }
        return total;
    }

    private boolean meetsDynamoCriteria(String searchFilter, int from, int querySize, String sortBy) {
        return !StringUtils.hasLength(searchFilter)
                && from == 0
                && querySize <= DYNAMO_THRESHOLD
                && sortBy.contains(SORT_BY_NUMBER);
    }
}

package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.customer.model.Account;
import com.amazonaws.saas.eks.customer.model.Customer;
import com.amazonaws.saas.eks.customer.model.enums.EntityStatus;
import com.amazonaws.saas.eks.customer.model.enums.EntityType;
import com.amazonaws.saas.eks.customer.model.search.AccountSearchResponse;
import com.amazonaws.saas.eks.exception.CustomerException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.opensearch._types.FieldSort;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchBoolPrefixQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.TermQuery;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public class AccountRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(AccountRepository.class);

    private static final String NUMBER_PREFIX = "ACC";
    private static final String SORT_BY_NUMBER = "number";
    private static final String SORT_BY_NAME = "name";
    private static final String SORT_BY_CREDIT_LIMIT = "credit_limit";
    private static final String SORT_BY_BALANCE = "balance";
    private static final String SORT_BY_DESC = "desc";

    /**
     * Method to save an Account for a tenant
     * @param account {@link Account to save}
     * @param tenantId Tenant ID
     * @return the saved {@link Account model}
     */
    public Account create(Account account, String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        if (!StringUtils.hasLength(account.getPartitionKey())) {
            account.setPartitionKey(EntityType.ACCOUNTS.getLabel());
        }
        if (!StringUtils.hasLength(account.getId())) {
            account.setId(String.valueOf(UUID.randomUUID()));
        }
        if (account.getCreated() == null) {
            account.setCreated(new Date());
        }
        if (account.getModified() == null) {
            account.setModified(account.getCreated());
        }
        account.setStatus(EntityStatus.ACTIVE.toString());
        try {
            int count = getLatestCounter(tenantId, EntityType.ACCOUNTS);
            account.setNumber(String.format("%s%04d", NUMBER_PREFIX, count));
            mapper.save(account);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Save Customer Account failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new CustomerException(message);
        }

        return account;
    }

    public AccountSearchResponse search(String tenantId,
                                        int from,
                                        int size,
                                        String filter,
                                        String sortBy,
                                        String customerId) {
        List<Query> filterQueries = new ArrayList<>();
        TermQuery statusQuery = new TermQuery.Builder()
                .field(Account.OpenSearch.FieldNames.STATUS_KEYWORD)
                .value(FieldValue.of(EntityStatus.ACTIVE.toString()))
                .build();
        filterQueries.add(statusQuery._toQuery());

        if (StringUtils.hasLength(customerId)) {
            TermQuery customerIdQuery = new TermQuery.Builder()
                    .field(Account.OpenSearch.FieldNames.CUSTOMER_ID_KEYWORD)
                    .value(FieldValue.of(customerId))
                    .build();
            filterQueries.add(customerIdQuery._toQuery());
        }

        List<Query> matchQueries = new ArrayList<>();
        int minShouldMatch = 0;
        if (StringUtils.hasLength(filter)) {
            MatchBoolPrefixQuery nameQuery = new MatchBoolPrefixQuery.Builder()
                    .field(Account.OpenSearch.FieldNames.NAME)
                    .query(filter)
                    .build();
            matchQueries.add(nameQuery._toQuery());

            MatchBoolPrefixQuery numberQuery = new MatchBoolPrefixQuery.Builder()
                    .field(Account.OpenSearch.FieldNames.NUMBER)
                    .query(filter)
                    .build();
            matchQueries.add(numberQuery._toQuery());
            minShouldMatch = 1;
        }

        List<SortOptions> sorting = new ArrayList<>();
        if (StringUtils.hasLength(sortBy)) {
            String field = getSearchSortField(sortBy);
            if (StringUtils.hasLength(field)) {
                SortOrder order = sortBy.contains(SORT_BY_DESC) ? SortOrder.Desc : SortOrder.Asc;
                FieldSort fieldSort = FieldSort.of(f -> f.field(field).order(order));
                sorting.add(SortOptions.of(s -> s.field(fieldSort)));
            }
        }

        BoolQuery boolQuery = new BoolQuery.Builder()
                .should(matchQueries)
                .filter(filterQueries)
                .minimumShouldMatch(String.valueOf(minShouldMatch))
                .build();

        SearchRequest req = SearchRequest.of(s -> s
                .index(Account.OpenSearch.getIndex(tenantId))
                .query(boolQuery._toQuery())
                .sort(sorting)
                .from(from)
                .size(size));

        SearchResponse<JsonNode> results;
        try {
            results = openSearchClient.search(req, JsonNode.class);
        } catch (Exception ex) {
            String message = String.format("Error fetching accounts from openSearch. TenantId %s", tenantId);
            logger.error(message, ex);
            throw new CustomerException(message);
        }

        AccountSearchResponse response = new AccountSearchResponse();
        response.setAccounts(convertSearchResultsToModels(results, Account.class, tenantId));
        response.setCount(results.hits().total().value());
        return response;
    }

    private String getSearchSortField(String sortBy) {
        if (sortBy.toLowerCase().contains(SORT_BY_NUMBER)) {
            return Customer.OpenSearch.FieldNames.NUMBER_KEYWORD;
        }
        if (sortBy.toLowerCase().contains(SORT_BY_NAME)) {
            return Customer.OpenSearch.FieldNames.NAME_KEYWORD;
        }
        if (sortBy.toLowerCase().contains(SORT_BY_CREDIT_LIMIT)) {
            return Customer.OpenSearch.FieldNames.CREDIT_LIMIT_KEYWORD;
        }
        if (sortBy.toLowerCase().contains(SORT_BY_BALANCE)) {
            return Customer.OpenSearch.FieldNames.BALANCE_KEYWORD;
        }

        return "";
    }
}

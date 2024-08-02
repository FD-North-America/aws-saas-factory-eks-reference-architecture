package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.model.dynamo.DynamoDbStreamRecord;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.product.model.Product;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.CountRequest;
import org.opensearch.client.opensearch.core.CountResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.amazonaws.saas.eks.config.DynamoDBConfig.getAmazonDynamoDBLocalClient;

public class BaseRepository {

    protected static final String AGGREGATION_SCRIPT = "Double.parseDouble(doc['%s'].value)";

    protected static final int ROUND_SETTING = 2;

    @Autowired
    protected OpenSearchClient openSearchClient;

    @Autowired
    protected DynamoDBMapper mapper;

    protected DynamoDBMapper dynamoDBOrderMapper(String tenantId) {
        return dynamoDBMapper(Order.TABLE_NAME, tenantId);
    }

    protected DynamoDBMapper dynamoDBProductMapper(String tenantId) {
        return dynamoDBMapper(Product.TABLE_NAME, tenantId);
    }

    protected DynamoDBMapper dynamoDBMapper(String tableRootName, String tenantId) {
        String tableName = String.format("%s-%s", tableRootName, tenantId);
        DynamoDBMapperConfig dbMapperConfig = new DynamoDBMapperConfig.Builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName))
                .build();
        return new DynamoDBMapper(getAmazonDynamoDBLocalClient(), dbMapperConfig);
    }

    protected <T> List<T> convertSearchResultsToModels(SearchResponse<JsonNode> results, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        List<DynamoDbStreamRecord> records = new ArrayList<>();
        for(int i = 0; i < results.hits().hits().size(); i++) {
            DynamoDbStreamRecord r = objectMapper.convertValue(results.hits().hits().get(i).source(),
                    DynamoDbStreamRecord.class);
            records.add(r);
        }

        List<Map<String, AttributeValue>> dynamoDbProductAttributes = records
                .stream()
                .map(DynamoDbStreamRecord::getNewImage)
                .collect(Collectors.toList());
        return mapper.marshallIntoObjects(clazz, dynamoDbProductAttributes);
    }

    protected int getOpenSearchItemCount(String index, Query query) throws IOException, OpenSearchException {
        CountRequest countRequest = CountRequest.of(c -> c.index(index).query(query));
        CountResponse countResponse = openSearchClient.count(countRequest);
        return (int) countResponse.count();
    }
}

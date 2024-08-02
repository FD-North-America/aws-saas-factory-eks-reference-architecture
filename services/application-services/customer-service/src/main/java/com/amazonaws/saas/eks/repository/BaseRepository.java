package com.amazonaws.saas.eks.repository;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.saas.eks.customer.model.Counter;
import com.amazonaws.saas.eks.customer.model.Customer;
import com.amazonaws.saas.eks.customer.model.enums.EntityType;
import com.amazonaws.saas.eks.customer.model.search.DynamoDbStreamRecord;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseRepository {

    @Autowired
    protected OpenSearchClient openSearchClient;

    protected DynamoDBMapper dynamoDBMapper(String tenantId) {
        String tableName = String.format("%s-%s", Customer.TABLE_NAME, tenantId);
        DynamoDBMapperConfig dbMapperConfig = new DynamoDBMapperConfig.Builder()
                .withTableNameOverride(
                        DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName)
                ).build();

        AmazonDynamoDBClient dynamoDBClient = getAmazonDynamoDBLocalClient();
        return new DynamoDBMapper(dynamoDBClient, dbMapperConfig);
    }

    /**
     * Returns the newest counter value for the Entity type to use for IDs
     * @param tenantId String
     * @param entityType {@link EntityType}
     * @return Latest integer value for that entity
     */
    protected int getLatestCounter(String tenantId, EntityType entityType) {
        AmazonDynamoDB client = getAmazonDynamoDBLocalClient();

        Map<String, AttributeValue> keyMap = new HashMap<>();
        keyMap.put(Counter.DbAttrNames.PARTITION_KEY, new AttributeValue().withS(EntityType.COUNTER.getLabel()));
        keyMap.put(Counter.DbAttrNames.SORT_KEY, new AttributeValue().withS(entityType.getLabel()));

        Map<String, String> ean = new HashMap<>();
        ean.put("#"+ Counter.DbAttrNames.COUNT, Counter.DbAttrNames.COUNT);

        Map<String, AttributeValue> valMap = new HashMap<>();
        valMap.put(":val", new AttributeValue().withN("1"));

        UpdateItemRequest request = new UpdateItemRequest()
                .withTableName(String.format("%s-%s", Customer.TABLE_NAME, tenantId))
                .withKey(keyMap)
                .withExpressionAttributeNames(ean)
                .withUpdateExpression("ADD #Count :val")
                .withExpressionAttributeValues(valMap)
                .withReturnValues(ReturnValue.UPDATED_NEW);
        UpdateItemResult result = client.updateItem(request);
        return Integer.parseInt(result.getAttributes().get(Counter.DbAttrNames.COUNT).getN());
    }

    /**
     * Converts the JSON nodes returned from OpenSearch to their respective Java objects
     * @param results OpenSearch results
     * @param clazz Java Class to convert to
     * @param tenantId TenantId
     * @return List of the converted java objects
     * @param <T> Specific Java Model
     */
    protected <T> List<T> convertSearchResultsToModels(SearchResponse<JsonNode> results, Class<T> clazz, String tenantId) {
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
        return dynamoDBMapper(tenantId).marshallIntoObjects(clazz, dynamoDbProductAttributes);
    }

    /**
     * Helper method for DynamoDBMapper
     * @return AmazonDynamoDBClient
     */
    private AmazonDynamoDBClient getAmazonDynamoDBLocalClient() {
        return (AmazonDynamoDBClient) AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain()).build();
    }
}

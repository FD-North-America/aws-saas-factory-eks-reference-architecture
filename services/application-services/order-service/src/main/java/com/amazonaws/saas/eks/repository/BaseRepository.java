package com.amazonaws.saas.eks.repository;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.order.model.search.DynamoDbStreamRecord;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensearch.client.opensearch.core.SearchResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseRepository {
    protected DynamoDBMapper dynamoDBMapper(String tenantId) {
        String tableName = String.format("%s-%s", Order.TABLE_NAME, tenantId);
        DynamoDBMapperConfig dbMapperConfig = new DynamoDBMapperConfig.Builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName)).build();

        AmazonDynamoDBClient dynamoClient = getAmazonDynamoDBLocalClient();
        return new DynamoDBMapper(dynamoClient, dbMapperConfig);
    }

    /**
     * Helper method for DynamoDBMapper
     * @return AmazonDynamoDBClient
     */
    private AmazonDynamoDBClient getAmazonDynamoDBLocalClient() {
        return (AmazonDynamoDBClient) AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain()).build();
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
}

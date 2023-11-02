package com.amazonaws.saas.eks.repository;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.saas.eks.customer.model.Customer;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

public class BaseRepository {
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
     * Helper method for DynamoDBMapper
     * @return AmazonDynamoDBClient
     */
    private AmazonDynamoDBClient getAmazonDynamoDBLocalClient() {
        return (AmazonDynamoDBClient) AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain()).build();
    }
}

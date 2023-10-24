package com.amazonaws.saas.eks.repository;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

public class BaseRepository {
    /**
     * Helper method for DynamoDBMapper
     * @return AmazonDynamoDBClient
     */
    private AmazonDynamoDBClient getAmazonDynamoDBLocalClient() {
        return (AmazonDynamoDBClient) AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain()).build();
    }
}

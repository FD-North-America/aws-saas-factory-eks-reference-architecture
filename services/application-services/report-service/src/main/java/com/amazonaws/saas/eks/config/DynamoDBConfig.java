package com.amazonaws.saas.eks.config;

import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.springframework.context.annotation.Primary;

@Configuration
public class DynamoDBConfig {

	@Bean(name = "dynamoDBMapper")
	@Primary
	public DynamoDBMapper dynamoDBMapper() {
		DynamoDBMapperConfig dbMapperConfig = new DynamoDBMapperConfig.Builder().build();
		AmazonDynamoDB dynamoClient = getAmazonDynamoDBLocalClient();
		return new DynamoDBMapper(dynamoClient, dbMapperConfig);
	}

	public static AmazonDynamoDB getAmazonDynamoDBLocalClient() {
		return AmazonDynamoDBClientBuilder.standard().withCredentials(new DefaultAWSCredentialsProviderChain()).build();
	}
}

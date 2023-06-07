package com.amazonaws.saas.eks.config;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;

@Configuration
public class OpenSearchConfig {
    @Value("${aoss.host}")
    private String host;

    private static final Region REGION = Region.US_EAST_1;
    private static final String SIGNING_NAME = "aoss";

    @Bean(name = "openSearchClient")
    @Primary
    public OpenSearchClient openSearchClient() {
        SdkHttpClient httpClient = ApacheHttpClient.builder().build();
        return new OpenSearchClient(
                new AwsSdk2Transport(
                        httpClient,
                        host,
                        SIGNING_NAME,
                        REGION,
                        AwsSdk2TransportOptions.builder().build()));
    }
}

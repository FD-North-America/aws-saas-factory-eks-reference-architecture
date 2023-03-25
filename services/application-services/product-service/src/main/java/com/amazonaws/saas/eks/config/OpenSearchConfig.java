package com.amazonaws.saas.eks.config;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;

@Configuration
public class OpenSearchConfig {

    // TODO: MOVE TO CONFIG
    private static final String HOST = "iiaunxut51eeses1x1h5.us-east-1.aoss.amazonaws.com";
    private static final String SIGNING_NAME = "aoss";

    @Bean(name = "openSearchClient")
    @Primary
    public OpenSearchClient openSearchClient() {
        SdkHttpClient httpClient = ApacheHttpClient.builder().build();
        return new OpenSearchClient(
                new AwsSdk2Transport(
                        httpClient,
                        HOST,
                        SIGNING_NAME,
                        Region.US_EAST_1,
                        AwsSdk2TransportOptions.builder().build()));
    }
}

package com.amazonaws.saas.eks.clients.product;

import com.amazonaws.saas.eks.auth.client.AuthInterceptor;
import com.amazonaws.saas.eks.clients.product.dto.requests.PricingRequestParams;
import com.amazonaws.saas.eks.clients.product.dto.responses.PricingResponse;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "products",
        url = "${spring.cloud.openfeign.client.config.products.url}",
        configuration = ProductServiceClient.Config.class)
public interface ProductServiceClient {

    class Config {
        @Bean
        public RequestInterceptor authInterceptor(
                @Value("${auth.url}") final String authUrl,
                @Value("${auth.clientId}") final String clientId,
                @Value("${auth.clientSecret}") final String clientSecret) {
            return new AuthInterceptor(authUrl, clientId, clientSecret);
        }
    }

    @PostMapping(value = "{tenantId}/products/pricing", produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<PricingResponse> getPricingDetails(@PathVariable String tenantId,
                                                      @RequestBody PricingRequestParams params);
}

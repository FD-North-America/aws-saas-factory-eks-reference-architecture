package com.amazonaws.saas.eks.clients.cardsecure;

import com.amazonaws.saas.eks.payment.clients.cardsecure.dto.requests.ClientTokenizeRequest;
import com.amazonaws.saas.eks.payment.clients.cardsecure.dto.responses.ClientTokenizeResponse;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "cardsecure",
        url = "https://this-is-a-placeholder.com",
        configuration = CardSecureClientConfig.class
)
@Headers("Content-Type: application/json")
public interface CardSecureClient {
    @PostMapping(value = "/tokenize", produces = {MediaType.APPLICATION_JSON_VALUE})
    ClientTokenizeResponse tokenize(@RequestBody ClientTokenizeRequest request);
}

package com.amazonaws.saas.eks.clients.cardconnect;

import com.amazonaws.saas.eks.payment.clients.cardconnect.dto.requests.ClientAuthRequest;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "cardconnect", url = "https://this-is-a-placeholder.com", configuration = CardConnectClientConfig.class)
@Headers("Content-Type: application/json")
public interface CardConnectClient {
    @PostMapping(value = "/auth", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    ResponseEntity<Object> auth(@RequestBody ClientAuthRequest request);
}

package com.amazonaws.saas.eks.clients;

import com.amazonaws.saas.eks.settings.dto.responses.AddressResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "settings",
        url = "${spring.cloud.openfeign.client.config.address.url}")
public interface AddressServiceClient {
    @GetMapping(value = "addresses", produces = { MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<AddressResponse> getAddress(@RequestParam String state,
                                               @RequestParam String city);
}

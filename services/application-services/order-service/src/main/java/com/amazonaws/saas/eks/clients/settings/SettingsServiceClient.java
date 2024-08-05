package com.amazonaws.saas.eks.clients.settings;

import com.amazonaws.saas.eks.clients.ClientConfig;
import com.amazonaws.saas.eks.settings.dto.responses.ListSalesTaxSettingsResponse;
import com.amazonaws.saas.eks.settings.dto.responses.POSSettingsResponse;
import com.amazonaws.saas.eks.settings.dto.responses.PurchasingSettingsResponse;
import com.amazonaws.saas.eks.settings.dto.responses.ReasonCodesSettingsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "settings",
        url = "${spring.cloud.openfeign.client.config.settings.url}",
        configuration = ClientConfig.class)
public interface SettingsServiceClient {
    @GetMapping(value = "{tenantId}/settings/pos", produces = { MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<POSSettingsResponse> getPOSSettings(@PathVariable String tenantId);

    @GetMapping(value = "{tenantId}/settings/pos/{type}/next-sequence", produces = { MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<String> getPOSNextSequence(@PathVariable String tenantId, @PathVariable String type);

    @GetMapping(value = "{tenantId}/settings/sales-tax", produces = { MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<ListSalesTaxSettingsResponse> getAllSalesTax(@PathVariable String tenantId,
                                                                @RequestParam String filter,
                                                                @RequestParam String jurisdiction);

    @GetMapping(value = "{tenantId}/settings/reason-codes", produces = { MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<ReasonCodesSettingsResponse> getReasonCodes(@PathVariable String tenantId);

    @GetMapping(value = "{tenantId}/settings/purchasing", produces = {MediaType.APPLICATION_JSON_VALUE })
    PurchasingSettingsResponse getPurchasingSettings(@PathVariable String tenantId);
}
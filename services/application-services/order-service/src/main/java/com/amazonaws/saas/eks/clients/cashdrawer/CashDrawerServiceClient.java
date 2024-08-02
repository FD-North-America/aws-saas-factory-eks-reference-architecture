package com.amazonaws.saas.eks.clients.cashdrawer;

import com.amazonaws.saas.eks.cashdrawer.dto.responses.CashDrawerResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.ListCashDrawersResponse;
import com.amazonaws.saas.eks.clients.ClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cashdrawers",
        url = "${spring.cloud.openfeign.client.config.cashdrawers.url}",
        configuration = ClientConfig.class)
public interface CashDrawerServiceClient {
    /**
     * Calls CashDrawerService to fetch a cash drawer details
     * @param tenantId Tenant ID
     * @param cashDrawerId Cash Drawer ID
     * @return {@link CashDrawerResponse}
     */
    @GetMapping(value = "{tenantId}/cashdrawers/{cashDrawerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<CashDrawerResponse> get(@PathVariable("tenantId") String tenantId,
                                           @PathVariable("cashDrawerId") String cashDrawerId);

    /**
     * Calls CashDrawerService to fetch cash drawers assigned by the username
     * @param tenantId TenantID
     * @param username Username of the Assigned User
     * @return {@link ListCashDrawersResponse}
     */
    @GetMapping(value = "{tenantId}/cashdrawers/users/{username}", produces = { MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<ListCashDrawersResponse> getByAssignedUser(@PathVariable("tenantId") String tenantId,
                                                              @PathVariable("username") String username);
}

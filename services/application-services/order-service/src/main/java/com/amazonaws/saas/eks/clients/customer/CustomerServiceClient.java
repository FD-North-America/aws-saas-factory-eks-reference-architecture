package com.amazonaws.saas.eks.clients.customer;

import com.amazonaws.saas.eks.cashdrawer.dto.responses.CashDrawerResponse;
import com.amazonaws.saas.eks.clients.ClientConfig;
import com.amazonaws.saas.eks.customer.dto.responses.certificate.CertificateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customers",
        url = "${spring.cloud.openfeign.client.config.customers.url}",
        configuration = ClientConfig.class)
public interface CustomerServiceClient {
    /**
     * Calls CustomerService to fetch a customer's certificate details
     * @param certId Certificate ID
     * @param tenantId Tenant ID
     * @return {@link CashDrawerResponse}
     */
    @GetMapping(value = "{tenantId}/customers/certs/{certId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<CertificateResponse> get(@PathVariable String certId, @PathVariable String tenantId);
}

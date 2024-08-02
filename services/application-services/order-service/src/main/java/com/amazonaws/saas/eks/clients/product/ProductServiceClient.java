package com.amazonaws.saas.eks.clients.product;

import com.amazonaws.saas.eks.clients.ClientConfig;
import com.amazonaws.saas.eks.product.dto.requests.product.PricingRequestParams;
import com.amazonaws.saas.eks.product.dto.requests.product.UpdateCountRequestParams;
import com.amazonaws.saas.eks.product.dto.responses.product.PricingResponse;
import com.amazonaws.saas.eks.product.dto.responses.product.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@FeignClient(name = "products",
        url = "${spring.cloud.openfeign.client.config.products.url}",
        configuration = ClientConfig.class)
public interface ProductServiceClient {
    /**
     * Calls ProductService to fetch product details
     * @param tenantId Tenant ID
     * @param productId product ID
     * @return {@link ProductResponse}
     */
    @GetMapping(value = "{tenantId}/products/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<ProductResponse> get(@PathVariable("tenantId") String tenantId,
                                        @PathVariable("productId") String productId);

    /**
     * Calls ProductService to fetch pricing details for the specified products
     * @param tenantId Tenant ID
     * @param params {@link PricingRequestParams}
     * @return {@link PricingResponse}
     */
    @PostMapping(value = "{tenantId}/products/pricing", produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<PricingResponse> getPricingDetails(@PathVariable String tenantId,
                                                      @RequestBody PricingRequestParams params);

    /**
     * Calls ProductService to update the QuantityOnHand values based off the given quantities
     * @param tenantId Tenant ID
     * @param request {@link UpdateCountRequestParams}
     */
    @PutMapping(value = "{tenantId}/products/counts", produces = {MediaType.APPLICATION_JSON_VALUE})
    void updateProductCounts(@PathVariable String tenantId, @RequestBody UpdateCountRequestParams request);
}

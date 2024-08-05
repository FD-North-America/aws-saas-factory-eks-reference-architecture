package com.amazonaws.saas.eks.clients.payments;

import com.amazonaws.saas.eks.clients.ClientConfig;
import com.amazonaws.saas.eks.payment.dto.requests.AuthCardRequest;
import com.amazonaws.saas.eks.payment.dto.requests.ConnectRequest;
import com.amazonaws.saas.eks.payment.dto.responses.AuthCreatedResponse;
import com.amazonaws.saas.eks.payment.dto.responses.ConnectResponse;
import com.amazonaws.saas.eks.payment.dto.responses.ListOrderPaymentsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payments",
        url = "${spring.cloud.openfeign.client.config.payments.url}",
        configuration = ClientConfig.class)
public interface PaymentServiceClient {
    /**
     * Creates a session with the connected terminal
     * @param tenantId Tenant ID
     * @param request {@link ConnectRequest}
     * @return Connect Response
     */
    @PostMapping(value = "{tenantId}/payments/connect", produces = { MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<ConnectResponse> connect(@PathVariable String tenantId, @RequestBody ConnectRequest request);

    /**
     * Authorizes a transaction with the given Card
     * @param session CardConnect Session token
     * @param tenantId Tenant ID
     * @param request {@link AuthCardRequest}
     * @return Auth Card Response
     */
    @PostMapping(value = "{tenantId}/payments/authCard", produces = { MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<AuthCreatedResponse> authCard(@RequestHeader("X-Session") String session,
                                                 @PathVariable String tenantId,
                                                 @RequestBody AuthCardRequest request);

    /**
     * Given an OrderNumber, returns any payments associated with that order
     * @param tenantId Tenant ID
     * @param orderNumber Order Number (Not Order Id)
     * @return {@link ListOrderPaymentsResponse}
     */
    @GetMapping(value = "{tenantId}/payments/{orderNumber}", produces = { MediaType.APPLICATION_JSON_VALUE })
    ListOrderPaymentsResponse getOrderPayments(@PathVariable String tenantId, @PathVariable String orderNumber);
}

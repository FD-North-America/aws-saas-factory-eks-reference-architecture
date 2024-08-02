package com.amazonaws.saas.eks.util;

import com.amazonaws.saas.eks.clients.cardpointe.CardPointeServiceClient;
import com.amazonaws.saas.eks.payment.dto.requests.AuthCardRequest;
import com.amazonaws.saas.eks.payment.mapper.PaymentMapper;
import com.amazonaws.saas.eks.payment.model.Transaction;
import com.amazonaws.saas.eks.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AsyncUtils {
    private static final Logger logger = LogManager.getLogger(AsyncUtils.class);

    @Autowired
    private CardPointeServiceClient paymentClient;

    @Autowired
    private TransactionRepository transactionRepository;

    @Async
    public void startAuthProcess(String tenantId,
                                 String merchantId,
                                 String hsn,
                                 String sessionKey,
                                 AuthCardRequest authCardRequest) {
        com.amazonaws.saas.eks.payment.clients.cardpointe.dto.requests.AuthCardRequest request = PaymentMapper
                .INSTANCE
                .apiAuthCardRequestToClientAuthCardRequest(authCardRequest);
        request.setMerchantId(merchantId);
        request.setHsn(hsn);
        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = paymentClient.authCard(sessionKey, request);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        saveTransactionForOrder(tenantId, request.getMerchantId(), request.getHsn(), "AuthCard",
                authCardRequest, response, requestDate, responseDate, authCardRequest.getOrderId());
    }

    private void saveTransactionForOrder(String tenantId, String merchantId, String hsn, String type, Object requestBody,
                                         ResponseEntity<Object> response, Date requestDate, Date responseDate, String orderNumber) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper om = converter.getObjectMapper();

        Transaction transaction = new Transaction();
        transaction.setMerchantId(merchantId);
        transaction.setHsn(hsn);
        transaction.setType(type);
        transaction.setOrderNumber(orderNumber);

        try {
            if (requestBody != null) {
                transaction.setRequestBody(om.writeValueAsString(requestBody));
            }
        } catch (JsonProcessingException ex) {
            logger.error("Error writing value of request body", ex);
        }
        try {
            if (response.getBody() != null) {
                transaction.setResponseBody(om.writeValueAsString(response.getBody()));
            }
        } catch (JsonProcessingException ex) {
            logger.error("Error writing value of response body", ex);
        }
        transaction.setStatus(String.valueOf(response.getStatusCodeValue()));
        transaction.setRequestDate(Utils.toISO8601UTC(requestDate));
        transaction.setResponseDate(Utils.toISO8601UTC(responseDate));

        transactionRepository.insert(tenantId, transaction);
    }

    private ResponseEntity<Object> handleFeignException(FeignException ex) {
        logger.error(ex);
        Object response = null;
        try {
            response = new ObjectMapper().readValue(ex.contentUTF8(), Object.class);
        } catch (Exception ex2) {
            logger.error(ex2);
        }
        return ResponseEntity.status(ex.status()).body(response);
    }
}

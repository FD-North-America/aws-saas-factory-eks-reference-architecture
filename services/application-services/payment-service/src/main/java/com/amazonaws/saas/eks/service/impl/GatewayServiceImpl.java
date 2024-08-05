package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.clients.cardconnect.CardConnectClient;
import com.amazonaws.saas.eks.clients.cardsecure.CardSecureClient;
import com.amazonaws.saas.eks.payment.clients.cardconnect.dto.requests.ClientAuthRequest;
import com.amazonaws.saas.eks.payment.clients.cardsecure.dto.requests.ClientTokenizeRequest;
import com.amazonaws.saas.eks.payment.clients.cardsecure.dto.responses.ClientTokenizeResponse;
import com.amazonaws.saas.eks.payment.dto.requests.gateway.AuthRequest;
import com.amazonaws.saas.eks.payment.mapper.PaymentMapper;
import com.amazonaws.saas.eks.payment.model.Transaction;
import com.amazonaws.saas.eks.repository.TransactionRepository;
import com.amazonaws.saas.eks.service.GatewayService;
import com.amazonaws.saas.eks.util.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GatewayServiceImpl implements GatewayService {
    private static final Logger logger = LogManager.getLogger(GatewayServiceImpl.class);

    @Autowired
    private CardConnectClient client;

    @Autowired
    private CardSecureClient cardSecureClient;

    @Autowired
    private TransactionRepository repository;

    @Override
    public ResponseEntity<Object> auth(String tenantId, AuthRequest request) {
        ClientAuthRequest clientRequest = PaymentMapper.INSTANCE.apiAuthRequestToClientAuthRequest(request);

        // Securing account before authorizing
        String token = tokenize(tenantId, request.getAccount());
        clientRequest.setAccount(token);

        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = client.auth(clientRequest);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }

        Date responseDate = new Date();
        saveTransaction(tenantId, request.getMerchId(), "Gateway Auth", request.getOrderId(),
                request, response, requestDate, responseDate);
        return response;
    }

    private String tokenize(String tenantId, String account) {
        ClientTokenizeRequest request = ClientTokenizeRequest.builder().account(account).build();

        ClientTokenizeResponse response;
        try {
            response = cardSecureClient.tokenize(request);
        } catch (FeignException ex) {
            logger.error("Error tokenizing account. TenantID {}", tenantId, ex);
            throw ex;
        }

        return response.getToken();
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

    private void saveTransaction(String tenantId,
                                 String merchantId,
                                 String type,
                                 String orderNumber,
                                 Object requestBody,
                                 ResponseEntity<Object> response,
                                 Date requestDate,
                                 Date responseDate) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper om = converter.getObjectMapper();

        Transaction transaction = new Transaction();
        transaction.setMerchantId(merchantId);
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

        repository.insert(tenantId, transaction);
    }
}

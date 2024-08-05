package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.clients.cardpointe.CardPointeServiceClient;
import com.amazonaws.saas.eks.payment.clients.cardpointe.dto.requests.*;
import com.amazonaws.saas.eks.payment.dto.requests.ConnectRequest;
import com.amazonaws.saas.eks.payment.dto.responses.*;
import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.handlerinterceptor.SessionHolder;
import com.amazonaws.saas.eks.payment.mapper.PaymentMapper;
import com.amazonaws.saas.eks.payment.model.Transaction;
import com.amazonaws.saas.eks.repository.TransactionRepository;
import com.amazonaws.saas.eks.secretsmanager.SecretsClient;
import com.amazonaws.saas.eks.service.PaymentService;
import com.amazonaws.saas.eks.util.AsyncUtils;
import com.amazonaws.saas.eks.util.EncryptionUtils;
import com.amazonaws.saas.eks.util.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    private CardPointeServiceClient paymentClient;

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Autowired
    private SessionHolder sessionHolder;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SecretsClient secretsClient;

    @Autowired
    private AsyncUtils asyncUtils;

    @Override
    public ResponseEntity<Object> connect(String tenantId, ConnectRequest request) {
        Date requestDate = new Date();
        String headerText = "X-CardConnect-SessionKey";
        Map<String, Collection<String>> headers = null;
        ResponseEntity<Object> response = null;
        try {
            try (Response r = paymentClient.connect(
                    PaymentMapper.INSTANCE.apiConnectRequestToClientConnectRequest(request)
            )) {
                headers = r.headers();
                if (headers == null || headers.get(headerText) == null) {
                    response = ResponseEntity.status(r.status()).body(r.body());
                }
            }
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        if (response == null) {
            ConnectResponse connectResponse = new ConnectResponse();

            List<String> sessionHeader = new ArrayList<>(headers.get(headerText));
            String[] sessionValues = sessionHeader.get(0).split(";");
            String sessionKey = sessionValues[0];
            Date sessionExpiryDate = Utils.fromISO8601UTC(sessionValues[1].substring(8));
            logger.info("Session: Key={}, Expires={}", sessionKey, sessionExpiryDate);

            String session = encryptionUtils.encrypt(
                    String.format("%s:%s:%s", request.getMerchantId(), request.getHsn(), sessionKey));

            connectResponse.setSession(session);
            connectResponse.setExpiryDate(sessionExpiryDate);
            response = ResponseEntity.ok(connectResponse);
        }

        saveTransaction(tenantId, request.getMerchantId(), request.getHsn(), "Connect", request, response,
                requestDate, responseDate);

        return response;
    }

    @Override
    public ResponseEntity<Object> ping(String tenantId) {
        PingRequest request = new PingRequest();
        request.setMerchantId(sessionHolder.getMerchantId());
        request.setHsn(sessionHolder.getHsn());

        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = paymentClient.ping(request);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        saveTransaction(tenantId, request.getMerchantId(), request.getHsn(), "Ping", null, response,
                requestDate, responseDate);

        return response;
    }

    @Override
    public ResponseEntity<Object> listTerminals(String tenantId) {
        ListTerminalsRequest request = new ListTerminalsRequest();
        request.setMerchantId(sessionHolder.getMerchantId());

        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = paymentClient.listTerminals(request);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        saveTransaction(tenantId, request.getMerchantId(), null, "ListTerminals", null, response,
                requestDate, responseDate);

        return response;
    }

    @Override
    public ResponseEntity<Object> disconnect(String tenantId) {
        DisconnectRequest request = new DisconnectRequest();
        request.setMerchantId(sessionHolder.getMerchantId());
        request.setHsn(sessionHolder.getHsn());

        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = paymentClient.disconnect(request);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        saveTransaction(tenantId, request.getMerchantId(), request.getHsn(), "Disconnect", null,
                response, requestDate, responseDate);

        return response;
    }

    @Override
    public ResponseEntity<Object> terminalDetails(String tenantId) {
        TerminalDetailsRequest request = new TerminalDetailsRequest();
        request.setMerchantId(sessionHolder.getMerchantId());
        request.setHsn(sessionHolder.getHsn());

        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = paymentClient.terminalDetails(request);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        saveTransaction(tenantId, request.getMerchantId(), request.getHsn(), "TerminalDetails", null,
                response, requestDate, responseDate);

        return response;
    }

    @Override
    public ResponseEntity<Object> display(String tenantId,
                                          com.amazonaws.saas.eks.payment.dto.requests.DisplayRequest displayRequest) {
        DisplayRequest request = new DisplayRequest();
        request.setMerchantId(sessionHolder.getMerchantId());
        request.setHsn(sessionHolder.getHsn());
        request.setText(displayRequest.getText());

        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = paymentClient.display(request);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        saveTransaction(tenantId, request.getMerchantId(), request.getHsn(), "Display", displayRequest,
                response, requestDate, responseDate);

        return response;
    }

    @Override
    public ResponseEntity<Object> readInput(String tenantId,
                                            com.amazonaws.saas.eks.payment.dto.requests.ReadInputRequest inputRequest) {
        ReadInputRequest request = PaymentMapper.INSTANCE.apiReadInputRequestToClientReadInputRequest(inputRequest);
        request.setMerchantId(sessionHolder.getMerchantId());
        request.setHsn(sessionHolder.getHsn());

        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = paymentClient.readInput(request);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        saveTransaction(tenantId, request.getMerchantId(), request.getHsn(), "ReadInput", inputRequest,
                response, requestDate, responseDate);

        return response;
    }

    @Override
    public ResponseEntity<Object> readSignature(
            String tenantId,
            com.amazonaws.saas.eks.payment.dto.requests.ReadSignatureRequest readSignatureRequest) {
        ReadSignatureRequest request = new ReadSignatureRequest();
        request.setMerchantId(sessionHolder.getMerchantId());
        request.setHsn(sessionHolder.getHsn());
        request.setPrompt(readSignatureRequest.getPrompt());

        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = paymentClient.readSignature(request);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        saveTransaction(tenantId, request.getMerchantId(), request.getHsn(), "ReadSignature", readSignatureRequest,
                response, requestDate, responseDate);

        return response;
    }

    @Override
    public ResponseEntity<Object> readConfirmation(
            String tenantId,
            com.amazonaws.saas.eks.payment.dto.requests.ReadConfirmationRequest readConfirmationRequest) {
        ReadConfirmationRequest request = new ReadConfirmationRequest();
        request.setMerchantId(sessionHolder.getMerchantId());
        request.setHsn(sessionHolder.getHsn());
        request.setPrompt(readConfirmationRequest.getPrompt());
        request.setBeep(readConfirmationRequest.getBeep());

        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = paymentClient.readConfirmation(request);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        saveTransaction(tenantId, request.getMerchantId(), request.getHsn(), "ReadConfirmation",
                readConfirmationRequest, response, requestDate, responseDate);

        return response;
    }

    @Override
    public ResponseEntity<Object> cancel(String tenantId) {
        CancelRequest request = new CancelRequest();
        request.setMerchantId(sessionHolder.getMerchantId());
        request.setHsn(sessionHolder.getHsn());

        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = paymentClient.cancel(request);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        saveTransaction(tenantId, request.getMerchantId(), request.getHsn(), "Cancel",
                null, response, requestDate, responseDate);

        return response;
    }

    @Override
    public ResponseEntity<Object> readManual(
            String tenantId,
            com.amazonaws.saas.eks.payment.dto.requests.ReadManualRequest readManualRequest) {
        ReadManualRequest request = PaymentMapper.INSTANCE.apiReadManualRequestToClientReadManualRequest(readManualRequest);
        request.setMerchantId(sessionHolder.getMerchantId());
        request.setHsn(sessionHolder.getHsn());

        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = paymentClient.readManual(request);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        saveTransaction(tenantId, request.getMerchantId(), request.getHsn(), "ReadManual",
                readManualRequest, response, requestDate, responseDate);

        return response;
    }

    @Override
    public ResponseEntity<Object> readCard(
            String tenantId,
            com.amazonaws.saas.eks.payment.dto.requests.ReadCardRequest readCardRequest) {
        ReadCardRequest request = PaymentMapper.INSTANCE.apiReadCardRequestToClientReadCardRequest(readCardRequest);
        request.setMerchantId(sessionHolder.getMerchantId());
        request.setHsn(sessionHolder.getHsn());

        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = paymentClient.readCard(request);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        saveTransaction(tenantId, request.getMerchantId(), request.getHsn(), "ReadCard",
                readCardRequest, response, requestDate, responseDate);

        return response;
    }

    @Override
    public ResponseEntity<Object> authCard(
            String tenantId,
            com.amazonaws.saas.eks.payment.dto.requests.AuthCardRequest authCardRequest) {
        asyncUtils.startAuthProcess(tenantId, sessionHolder.getMerchantId(), sessionHolder.getHsn(), sessionHolder.getSessionKey(), authCardRequest);
        AuthCreatedResponse authResponse = new AuthCreatedResponse();
        authResponse.setOrderNumber(authCardRequest.getOrderId());
        return ResponseEntity.accepted().body(authResponse);
    }

    @Override
    public ResponseEntity<Object> authManual(
            String tenantId,
            com.amazonaws.saas.eks.payment.dto.requests.AuthManualRequest authManualRequest) {
        AuthManualRequest request = PaymentMapper.INSTANCE.apiAuthManualRequestToClientAuthManualRequest(authManualRequest);
        request.setMerchantId(sessionHolder.getMerchantId());
        request.setHsn(sessionHolder.getHsn());

        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = paymentClient.authManual(request);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        saveTransaction(tenantId, request.getMerchantId(), request.getHsn(), "AuthManual",
                authManualRequest, response, requestDate, responseDate);

        return response;
    }

    @Override
    public ResponseEntity<Object> tip(
            String tenantId,
            com.amazonaws.saas.eks.payment.dto.requests.TipRequest tipRequest) {
        TipRequest request = PaymentMapper.INSTANCE.apiTipRequestToClientTipRequest(tipRequest);
        request.setMerchantId(sessionHolder.getMerchantId());
        request.setHsn(sessionHolder.getHsn());

        Date requestDate = new Date();
        ResponseEntity<Object> response;
        try {
            response = paymentClient.tip(request);
        } catch (FeignException ex) {
            response = handleFeignException(ex);
        }
        Date responseDate = new Date();

        saveTransaction(tenantId, request.getMerchantId(), request.getHsn(), "Tip",
                tipRequest, response, requestDate, responseDate);

        return response;
    }

    @Override
    public ListOrderPaymentsResponse getOrderPayments(String tenantId, String orderNumber) throws JsonProcessingException {
        List<Transaction> transactions = transactionRepository.getByOrderNumber(tenantId, orderNumber);
        if (CollectionUtils.isEmpty(transactions)) {
            throw new EntityNotFoundException("No payments found for Order Number: " + orderNumber);
        }
        ListOrderPaymentsResponse response = new ListOrderPaymentsResponse();
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        for (Transaction t : transactions) {
            OrderPaymentResponse orderPaymentResponse = PaymentMapper.INSTANCE.transactionToOrderPaymentResponse(t);
            if (StringUtils.hasLength(t.getResponseBody())) {
                orderPaymentResponse.setResponse(mapper.readValue(t.getResponseBody(), AuthResponse.class));
            }
            response.getPayments().add(orderPaymentResponse);
        }
        return response;
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

    private void saveTransaction(String tenantId, String merchantId, String hsn, String type, Object requestBody,
                                 ResponseEntity<Object> response, Date requestDate, Date responseDate) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper om = converter.getObjectMapper();

        Transaction transaction = new Transaction();
        transaction.setMerchantId(merchantId);
        transaction.setHsn(hsn);
        transaction.setType(type);
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
}

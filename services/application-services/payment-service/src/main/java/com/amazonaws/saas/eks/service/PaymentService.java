package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.payment.dto.requests.*;
import com.amazonaws.saas.eks.payment.dto.responses.ListOrderPaymentsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

public interface PaymentService {
    ResponseEntity<Object> connect(String tenantId, ConnectRequest request);

    ResponseEntity<Object> ping(String tenantId);

    ResponseEntity<Object> listTerminals(String tenantId);

    ResponseEntity<Object> disconnect(String tenantId);

    ResponseEntity<Object> terminalDetails(String tenantId);

    ResponseEntity<Object> display(String tenantId, DisplayRequest request);

    ResponseEntity<Object> readInput(String tenantId, ReadInputRequest readInputRequest);

    ResponseEntity<Object> readSignature(String tenantId, ReadSignatureRequest readSignatureRequest);

    ResponseEntity<Object> readConfirmation(String tenantId, ReadConfirmationRequest readConfirmationRequest);

    ResponseEntity<Object> cancel(String tenantId);

    ResponseEntity<Object> readManual(String tenantId, ReadManualRequest readManualRequest);

    ResponseEntity<Object> readCard(String tenantId, ReadCardRequest readCardRequest);

    ResponseEntity<Object> authCard(String tenantId, AuthCardRequest authCardRequest);

    ResponseEntity<Object> authManual(String tenantId, AuthManualRequest authManualRequest);

    ResponseEntity<Object> tip(String tenantId, TipRequest tipRequest);

    ListOrderPaymentsResponse getOrderPayments(String tenantId, String orderNumber) throws JsonProcessingException;
}

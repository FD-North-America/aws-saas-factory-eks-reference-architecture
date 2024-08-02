package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.payment.dto.requests.gateway.AuthRequest;
import org.springframework.http.ResponseEntity;

public interface GatewayService {
    ResponseEntity<Object> auth(String tenantId, AuthRequest request);
}

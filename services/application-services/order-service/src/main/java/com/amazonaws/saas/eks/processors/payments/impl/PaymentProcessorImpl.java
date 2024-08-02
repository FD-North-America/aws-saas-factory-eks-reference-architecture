package com.amazonaws.saas.eks.processors.payments.impl;

import com.amazonaws.saas.eks.clients.payments.PaymentServiceClient;
import com.amazonaws.saas.eks.exception.OrderException;
import com.amazonaws.saas.eks.payment.dto.requests.AuthCardRequest;
import com.amazonaws.saas.eks.payment.dto.requests.ConnectRequest;
import com.amazonaws.saas.eks.payment.dto.responses.ConnectResponse;
import com.amazonaws.saas.eks.processors.payments.PaymentProcessor;
import com.amazonaws.saas.eks.repository.SettingsRepository;
import com.amazonaws.saas.eks.settings.model.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.amazonaws.saas.eks.utils.Utils.roundValue;

@Service
public class PaymentProcessorImpl implements PaymentProcessor {
    @Autowired
    private PaymentServiceClient paymentServiceClient;

    @Autowired
    private SettingsRepository settingsRepository;

    @Override
    public void process(String tenantId, String orderNumber, BigDecimal amount) {
        var connectResponse = connectTerminal(tenantId);
        var authRequest = buildAuthRequest(orderNumber, amount);
        var response = paymentServiceClient.authCard(connectResponse.getSession(), tenantId, authRequest);
        if (response.getStatusCode() != HttpStatus.ACCEPTED) {
            throw new OrderException("Failed to start Card Authorization");
        }
    }

    private ConnectResponse connectTerminal(String tenantId) {
        Settings settings = settingsRepository.get(tenantId);
        ConnectRequest connectRequest = new ConnectRequest();
        connectRequest.setMerchantId(settings.getMerchantId());
        connectRequest.setHsn(settings.getDefaultHSN());
        connectRequest.setForce(true);
        var response = paymentServiceClient.connect(tenantId, connectRequest);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new OrderException("Cannot connect to terminal");
        }

        return response.getBody();
    }

    private AuthCardRequest buildAuthRequest(String orderNumber, BigDecimal amount) {
        var authRequest = new AuthCardRequest();
        authRequest.setAmount(formatCreditAmount(amount));
        authRequest.setOrderId(orderNumber);
        authRequest.setBeep(true);
        authRequest.setIncludeAmountDisplay(true);
        authRequest.setAid("credit");
        authRequest.setIncludeAVS(false);
        authRequest.setCapture(true);
        authRequest.setClearDisplayDelay(500);
        return authRequest;
    }

    private String formatCreditAmount(BigDecimal amount) {
        return roundValue(amount).toString().replace(".", "");
    }
}

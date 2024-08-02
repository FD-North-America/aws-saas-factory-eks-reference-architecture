package com.amazonaws.saas.eks.payment.mapper;

import com.amazonaws.saas.eks.payment.clients.cardconnect.dto.requests.ClientAuthRequest;
import com.amazonaws.saas.eks.payment.clients.cardpointe.dto.requests.*;
import com.amazonaws.saas.eks.payment.dto.requests.gateway.AuthRequest;
import com.amazonaws.saas.eks.payment.dto.responses.OrderPaymentResponse;
import com.amazonaws.saas.eks.payment.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    ConnectRequest apiConnectRequestToClientConnectRequest(com.amazonaws.saas.eks.payment.dto.requests.ConnectRequest request);
    ReadInputRequest apiReadInputRequestToClientReadInputRequest(com.amazonaws.saas.eks.payment.dto.requests.ReadInputRequest request);
    ReadManualRequest apiReadManualRequestToClientReadManualRequest(com.amazonaws.saas.eks.payment.dto.requests.ReadManualRequest request);
    ReadCardRequest apiReadCardRequestToClientReadCardRequest(com.amazonaws.saas.eks.payment.dto.requests.ReadCardRequest request);
    AuthCardRequest apiAuthCardRequestToClientAuthCardRequest(com.amazonaws.saas.eks.payment.dto.requests.AuthCardRequest request);
    AuthManualRequest apiAuthManualRequestToClientAuthManualRequest(com.amazonaws.saas.eks.payment.dto.requests.AuthManualRequest request);
    TipRequest apiTipRequestToClientTipRequest(com.amazonaws.saas.eks.payment.dto.requests.TipRequest request);
    OrderPaymentResponse transactionToOrderPaymentResponse(Transaction transaction);
    ClientAuthRequest apiAuthRequestToClientAuthRequest(AuthRequest request);
}

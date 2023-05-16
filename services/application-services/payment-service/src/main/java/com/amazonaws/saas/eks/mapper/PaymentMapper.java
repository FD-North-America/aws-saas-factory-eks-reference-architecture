package com.amazonaws.saas.eks.mapper;

import com.amazonaws.saas.eks.clients.cardpointe.dto.requests.*;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    ConnectRequest apiConnectRequestToClientConnectRequest(com.amazonaws.saas.eks.dto.requests.ConnectRequest request);
    ReadInputRequest apiReadInputRequestToClientReadInputRequest(com.amazonaws.saas.eks.dto.requests.ReadInputRequest request);
    ReadManualRequest apiReadManualRequestToClientReadManualRequest(com.amazonaws.saas.eks.dto.requests.ReadManualRequest request);
    ReadCardRequest apiReadCardRequestToClientReadCardRequest(com.amazonaws.saas.eks.dto.requests.ReadCardRequest request);
    AuthCardRequest apiAuthCardRequestToClientAuthCardRequest(com.amazonaws.saas.eks.dto.requests.AuthCardRequest request);
    AuthManualRequest apiAuthManualRequestToClientAuthManualRequest(com.amazonaws.saas.eks.dto.requests.AuthManualRequest request);
    TipRequest apiTipRequestToClientTipRequest(com.amazonaws.saas.eks.dto.requests.TipRequest request);
}

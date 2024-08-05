package com.amazonaws.saas.eks.payment.clients.cardsecure.dto.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientTokenizeRequest {
    private String account;
}

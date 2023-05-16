package com.amazonaws.saas.eks.clients.cardpointe.dto.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TipRequest {
    private String merchantId;

    private String hsn;

    private String prompt;

    private String amount;

    private List<Integer> tipPercentPresets;
}

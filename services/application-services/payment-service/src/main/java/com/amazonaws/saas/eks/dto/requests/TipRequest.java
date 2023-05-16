package com.amazonaws.saas.eks.dto.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TipRequest {
    private String prompt;

    private String amount;

    private List<Integer> tipPercentPresets;
}

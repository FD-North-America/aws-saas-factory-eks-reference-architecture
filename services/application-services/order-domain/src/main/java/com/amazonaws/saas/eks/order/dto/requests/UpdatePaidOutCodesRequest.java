package com.amazonaws.saas.eks.order.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class UpdatePaidOutCodesRequest {
    @NotNull
    @Getter
    @Setter
    private List<PaidOutCodeRequest> paidOutCodes = new ArrayList<>();
}

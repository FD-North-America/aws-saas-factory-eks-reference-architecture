package com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaidOutCodesResponse {
    private PaidOutCodeListResponse onlineRedeem = new PaidOutCodeListResponse();

    private PaidOutCodeListResponse instantRedeem = new PaidOutCodeListResponse();

    private PaidOutCodeListResponse misc = new PaidOutCodeListResponse();
}

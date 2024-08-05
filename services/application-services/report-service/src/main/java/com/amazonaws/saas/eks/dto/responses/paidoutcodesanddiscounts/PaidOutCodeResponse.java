package com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class PaidOutCodeResponse {
    private BigDecimal amount;

    private String code;

    private String repUser;

    private Date created;
}

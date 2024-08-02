package com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class DiscountResponse {
    private String code;

    private BigDecimal amount;

    private String orderNumber;

    private String repUser;

    private Date created;
}

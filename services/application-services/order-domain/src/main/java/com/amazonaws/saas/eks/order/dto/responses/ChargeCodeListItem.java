package com.amazonaws.saas.eks.order.dto.responses;

import com.amazonaws.saas.eks.order.util.MoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class ChargeCodeListItem {
    private String id;

    private String code;

    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal amount;

    private Date created;

    private Date modified;
}

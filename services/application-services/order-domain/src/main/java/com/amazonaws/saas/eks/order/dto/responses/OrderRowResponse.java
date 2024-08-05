package com.amazonaws.saas.eks.order.dto.responses;

import com.amazonaws.saas.eks.order.util.MoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrderRowResponse {
    private String id;
    private String number;
    private Date date;
    private String customerNumber;
    private String customerName;
    private String identifier;
    private String accountName;
    private String accountNumber;
    private String poNumber;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal total;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal tendered;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal amountDue;
    private Date expirationDate;
    private List<LineItemSummaryRow> lineItems = new ArrayList<>();
}

package com.amazonaws.saas.eks.dto.responses.orders;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderResponse {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private BigDecimal total;

    @Getter
    @Setter
    private String number;

    @Getter
    @Setter
    private BigDecimal subTotal;

    @Getter
    @Setter
    private BigDecimal taxTotal;

    @Getter
    @Setter
    private BigDecimal discountTotal;

    @Getter
    @Setter
    private BigDecimal taxableSubTotal;

    @Getter
    @Setter
    private BigDecimal nonTaxableSubTotal;

    @Getter
    @Setter
    private Date created;

    @Getter
    @Setter
    private Date modified;

    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    private List<LineItemResponse> lineItems = new ArrayList<>();

    @Getter
    @Setter
    private String cashDrawerId;

    @Getter
    @Setter
    private BigDecimal cashPaymentAmount;

    @Getter
    @Setter
    private BigDecimal creditPaymentAmount;

    @Getter
    @Setter
    private BigDecimal balanceDue;
}

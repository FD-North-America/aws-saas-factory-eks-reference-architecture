package com.amazonaws.saas.eks.order.dto.responses;

import com.amazonaws.saas.eks.order.dto.responses.reasoncodes.ReasonCodeItemResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private String id;

    private BigDecimal total;

    private String number;

    private BigDecimal subTotal;

    private BigDecimal taxTotal;

    private BigDecimal discountTotal;

    private BigDecimal taxableSubTotal;

    private BigDecimal nonTaxableSubTotal;

    private Date created;

    private Date modified;

    private String status;

    private List<LineItemResponse> lineItems = new ArrayList<>();

    private String cashDrawerId;

    private BigDecimal cashPaymentAmount;

    private BigDecimal creditPaymentAmount;

    private BigDecimal balanceDue;

    private List<TransactionResponse> transactions = new ArrayList<>();

    private Date paidDate;

    private List<PaidOutCodeResponse> paidOutCodeItems = new ArrayList<>();

    private List<ReasonCodeItemResponse> reasonCodes = new ArrayList<>();

    private String linkedOrderId;

    private String customerId;

    private String customerName;

    private String customerNumber;

    private String accountName;

    private String accountNumber;

    private String identifier;

    private String buyer;

    private BigDecimal balance;

    private BigDecimal creditLimit;

    private boolean delivered;

    private String type;

    private String deliveryId;

    private String taxId;
}

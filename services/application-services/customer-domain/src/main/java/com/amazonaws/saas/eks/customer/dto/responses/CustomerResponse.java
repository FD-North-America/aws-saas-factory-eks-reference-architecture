package com.amazonaws.saas.eks.customer.dto.responses;

import com.amazonaws.saas.eks.customer.util.MoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class CustomerResponse {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String loyaltyNumber;
    private Date created;
    private Date modified;
    private AddressResponse billingAddress;
    private AddressResponse shippingAddress;
    private String number;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal creditLimit;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal balance;
}

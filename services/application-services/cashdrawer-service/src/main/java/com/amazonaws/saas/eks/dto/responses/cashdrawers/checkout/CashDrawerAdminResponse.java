package com.amazonaws.saas.eks.dto.responses.cashdrawers.checkout;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

public class CashDrawerAdminResponse {
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String number;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    private Date created;

    @Getter
    @Setter
    private Date modified;

    @Getter
    @Setter
    private BigDecimal startUpAmount;

    @Getter
    @Setter
    private String assignedUser;

    @Getter
    @Setter
    private String checkoutRep;

    @Getter
    @Setter
    private BigDecimal checkoutAmounts;

    @Getter
    @Setter
    private Date clearedDate;

    @Getter
    @Setter
    private String clearedBy;
}

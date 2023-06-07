package com.amazonaws.saas.eks.dto.responses.cashdrawers;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CashDrawerResponse {
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
    private Boolean autoStartup;

    @Getter
    @Setter
    private BigDecimal startUpAmount;

    @Getter
    @Setter
    private String assignedUser;

    @Getter
    @Setter
    private List<CashDrawerTrayResponse> trays;
}

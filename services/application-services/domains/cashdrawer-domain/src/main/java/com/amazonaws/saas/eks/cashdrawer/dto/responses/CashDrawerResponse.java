package com.amazonaws.saas.eks.cashdrawer.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CashDrawerResponse {
    private String id;

    private String number;

    private String description;

    private String status;

    private Date created;

    private Date modified;

    private Boolean autoStartup;

    private BigDecimal startUpAmount;

    private String assignedUser;

    private Date startupDate;

    private String startupRep;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date checkoutDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String checkoutRep;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal checkoutAmounts;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date clearedDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String clearedBy;

    private List<CashDrawerTrayResponse> trays;

    private BigDecimal traysTotalAmount;

    private BigDecimal cashTotalAmount;

    private BigDecimal cardTotalAmount;

    private List<String> workstationIds;
}

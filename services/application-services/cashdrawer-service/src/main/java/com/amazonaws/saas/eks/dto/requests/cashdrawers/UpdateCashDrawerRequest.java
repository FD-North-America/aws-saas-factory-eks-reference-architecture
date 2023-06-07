package com.amazonaws.saas.eks.dto.requests.cashdrawers;

import com.amazonaws.saas.eks.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.model.CashDrawerTray;
import com.amazonaws.saas.eks.model.enums.CashDrawerStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UpdateCashDrawerRequest {
    @Getter
    @Setter
    private String description;

    @ValueOfEnum(enumClass = CashDrawerStatus.class)
    @Getter
    @Setter
    private String status;

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
    private String startupRep;

    @Getter
    @Setter
    private String checkoutRep;

    @Getter
    @Setter
    private List<CashDrawerTray> trays = new ArrayList<>();
}

package com.amazonaws.saas.eks.cashdrawer.dto.requests;

import com.amazonaws.saas.eks.cashdrawer.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerTray;
import com.amazonaws.saas.eks.cashdrawer.model.enums.CashDrawerStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UpdateCashDrawerRequest {
    private String description;

    @ValueOfEnum(enumClass = CashDrawerStatus.class)
    private String status;

    private Boolean autoStartup;

    private BigDecimal startUpAmount;

    private String assignedUser;

    private String startupRep;

    private String checkoutRep;

    private String clearedBy;

    private List<CashDrawerTray> trays = new ArrayList<>();

    private BigDecimal traysTotalAmount;

    private BigDecimal cashTotalAmount;

    private BigDecimal cardTotalAmount;

    private List<String> workstationIds;

    @JsonIgnore
    @AssertTrue(message = "You should set trays or traysTotalAmount but not both")
    private boolean isValidTrays() {
        return this.trays.isEmpty() || traysTotalAmount == null;
    }
}

package com.amazonaws.saas.eks.cashdrawer.dto.requests;

import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerTray;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreateCashDrawerRequest {
    @NotEmpty
    private String number;

    private String description;

    private Boolean autoStartup;

    private BigDecimal startUpAmount;

    private String assignedUser;

    private String startupRep;

    private String checkoutRep;

    private List<CashDrawerTray> trays = new ArrayList<>();

    private BigDecimal traysTotalAmount;

    @JsonIgnore
    @AssertTrue(message = "You should set trays or traysTotalAmount but not both")
    private boolean isValidTrays() {
        return this.trays.isEmpty() || traysTotalAmount == null;
    }
}

package com.amazonaws.saas.eks.dto.requests.cashdrawers;

import com.amazonaws.saas.eks.model.CashDrawerTray;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CreateCashDrawerRequest {
    @NotEmpty
    @Getter
    @Setter
    private String number;

    @Getter
    @Setter
    private String description;

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

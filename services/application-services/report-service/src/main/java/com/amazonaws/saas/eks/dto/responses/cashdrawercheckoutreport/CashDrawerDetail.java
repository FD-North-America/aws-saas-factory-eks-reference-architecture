package com.amazonaws.saas.eks.dto.responses.cashdrawercheckoutreport;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CashDrawerDetail {
    private String id;

    private String number;

    private String status;

    private Date startup;

    private Date checkout;

    private Date cleared;
}

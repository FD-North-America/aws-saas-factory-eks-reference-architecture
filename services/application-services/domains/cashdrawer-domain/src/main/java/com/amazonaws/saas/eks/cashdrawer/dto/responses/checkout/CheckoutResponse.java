package com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CheckoutResponse {
    private String id;
    private String cashDrawerId;
    private String cashDrawerNumber;
    private String status;
    private Date created;
}

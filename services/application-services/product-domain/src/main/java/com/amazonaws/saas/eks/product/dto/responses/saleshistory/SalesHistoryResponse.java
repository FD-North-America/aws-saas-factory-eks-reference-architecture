package com.amazonaws.saas.eks.product.dto.responses.saleshistory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesHistoryResponse {
    private String year;
    private float janAmount;
    private float febAmount;
    private float marchAmount;
    private float aprilAmount;
    private float mayAmount;
    private float juneAmount;
    private float julyAmount;
    private float augAmount;
    private float septAmount;
    private float octAmount;
    private float novAmount;
    private float decAmount;
}

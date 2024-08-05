package com.amazonaws.saas.eks.order.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CategoryOrder {
    private String orderId;
    private Date date;
}

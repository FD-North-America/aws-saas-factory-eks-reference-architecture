package com.amazonaws.saas.eks.dto.responses.returneditems;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class ReturnedItem {
    private String number; // product sku
    private String description; // product name
    private String invoice; // order number
    private Date date; // order created date
    private Integer quantity; // line item qty
    private BigDecimal priceUOM; // product pricing uom
    private BigDecimal extension; // qty * price
}

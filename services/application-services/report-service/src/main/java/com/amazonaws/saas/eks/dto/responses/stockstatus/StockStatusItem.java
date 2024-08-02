package com.amazonaws.saas.eks.dto.responses.stockstatus;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class StockStatusItem {
    @Getter
    @Setter
    private String sku;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String inventoryStatus;

    @Getter
    @Setter
    private String vendorName;

    @Getter
    @Setter
    private Float quantityOnHand;

    @Getter
    @Setter
    private String uom;

    @Getter
    @Setter
    private BigDecimal cost;

    @Getter
    @Setter
    private BigDecimal onHandValue;

    @Getter
    @Setter
    private BigDecimal onHandRetail;
}

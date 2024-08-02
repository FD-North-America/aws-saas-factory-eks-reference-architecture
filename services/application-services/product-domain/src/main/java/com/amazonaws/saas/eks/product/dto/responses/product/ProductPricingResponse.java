package com.amazonaws.saas.eks.product.dto.responses.product;

import com.amazonaws.saas.eks.product.dto.responses.uom.UOMResponse;
import com.amazonaws.saas.eks.product.dto.responses.volumepricing.VolumePricingResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductPricingResponse {
    private String id;
    private String name;
    private String categoryId;
    private String taxable;
    private BigDecimal retailPrice = BigDecimal.ZERO;
    private Integer quantity;
    private String sku;
    private String description;
    private List<VolumePricingResponse> volumePricing = new ArrayList<>();
    private List<UOMResponse> uomResponses = new ArrayList<>();
    private BigDecimal cost = BigDecimal.ZERO;
    private String uomId;
    private String uom;
    private String type;
    private boolean returnsAllowed;
}

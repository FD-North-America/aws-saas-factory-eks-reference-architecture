package com.amazonaws.saas.eks.product.model.vendor;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VendorSearchResponse {
    private List<Vendor> vendors;
    private long count;
}

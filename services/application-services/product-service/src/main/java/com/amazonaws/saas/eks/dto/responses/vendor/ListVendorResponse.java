package com.amazonaws.saas.eks.dto.responses.vendor;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ListVendorResponse {
    @Getter
    @Setter
    private List<VendorResponse> vendors = new ArrayList<>();

    public int getCount() { return vendors.size(); }
}

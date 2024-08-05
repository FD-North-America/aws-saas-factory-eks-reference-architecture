package com.amazonaws.saas.eks.product.dto.responses.vendor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListVendorItemResponse {
    private String id;
    private String name;
    private String number;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String status;
}

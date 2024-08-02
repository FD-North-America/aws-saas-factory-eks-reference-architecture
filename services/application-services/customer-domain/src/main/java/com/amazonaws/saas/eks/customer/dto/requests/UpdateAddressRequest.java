package com.amazonaws.saas.eks.customer.dto.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAddressRequest {
    private String street;
    private String suite;
    private String city;
    private String state;
    private String zip;
}

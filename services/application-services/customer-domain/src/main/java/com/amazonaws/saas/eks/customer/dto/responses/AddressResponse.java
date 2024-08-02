package com.amazonaws.saas.eks.customer.dto.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {
    private String street;
    private String suite;
    private String city;
    private String state;
    private String zip;
}

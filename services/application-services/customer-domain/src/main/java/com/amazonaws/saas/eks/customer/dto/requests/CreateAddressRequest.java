package com.amazonaws.saas.eks.customer.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateAddressRequest {
    private String street;
    private String suite;
    private String city;
    private String state;
    private String zip;
}

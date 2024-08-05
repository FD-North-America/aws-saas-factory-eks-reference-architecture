package com.amazonaws.saas.eks.customer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
    private String street;
    private String suite;
    private String city;
    private String state;
    private String zip;
}

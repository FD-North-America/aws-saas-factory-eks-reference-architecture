package com.amazonaws.saas.eks.settings.dto.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {
    private String state;
    private String city;
    private String county;
}

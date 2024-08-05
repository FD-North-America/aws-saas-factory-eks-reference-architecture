package com.amazonaws.saas.eks.product.dto.responses.vendor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorResponse {
    private String id;
    private String name;
    private String number;
    private String description;
    private String payeeVendorId;
    private String email;
    private String phone1;
    private String phone2;
    private String faxNumber;
    private String physicalAddress;
    private String physicalCity;
    private String physicalState;
    private String physicalZip;
    private String mailingAddress;
    private String mailingCity;
    private String mailingState;
    private String mailingZip;
    private EDIResponse edi;
    private String status;
    private Date created;
    private Date modified;
}

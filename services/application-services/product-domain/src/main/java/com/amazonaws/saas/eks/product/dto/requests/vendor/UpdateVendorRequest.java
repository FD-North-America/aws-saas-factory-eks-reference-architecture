package com.amazonaws.saas.eks.product.dto.requests.vendor;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdateVendorRequest {
    @NotBlank
    @Size(max = 25)
    private String name;

    @NotBlank
    @Size(max = 25)
    private String number;

    @Size(max = 40)
    private String description;

    private String payeeVendorId;

    @Email
    private String email;

    private String phone1;

    private String phone2;

    private String faxNumber;

    @NotBlank
    private String physicalAddress;

    @NotBlank
    private String physicalCity;

    @NotBlank
    private String physicalState;

    @NotBlank
    private String physicalZip;

    @NotBlank
    private String mailingAddress;

    @NotBlank
    private String mailingCity;

    @NotBlank
    private String mailingState;

    @NotBlank
    private String mailingZip;

    private EDIRequest edi;
}

package com.amazonaws.saas.eks.customer.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateCustomerRequest {
    @NotNull
    private String name;

    @NotNull
    @Email(message = "Please provide a valid email")
    private String email;

    @NotNull
    private String loyaltyNumber;

    @NotNull
    private UpdateAddressRequest billingAddress;

    private UpdateAddressRequest shippingAddress;

    private String phoneNumber;
}

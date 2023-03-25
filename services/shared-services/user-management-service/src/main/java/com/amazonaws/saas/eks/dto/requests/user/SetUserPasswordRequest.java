package com.amazonaws.saas.eks.dto.requests.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

public class SetUserPasswordRequest {
    /**
     * The password for the user.
     */
    @Pattern(regexp = "[\\S]+")
    @Getter
    @Setter
    private String password;

    /**
     * True if the password is permanent, False if it is temporary.
     */
    @Getter
    @Setter
    private boolean permanent;
}

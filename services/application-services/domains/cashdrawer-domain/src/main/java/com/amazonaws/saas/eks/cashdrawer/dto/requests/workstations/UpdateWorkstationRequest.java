package com.amazonaws.saas.eks.cashdrawer.dto.requests.workstations;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateWorkstationRequest {
    @NotNull
    private String name;
    private String ipAddress;
    private String hsn;
}

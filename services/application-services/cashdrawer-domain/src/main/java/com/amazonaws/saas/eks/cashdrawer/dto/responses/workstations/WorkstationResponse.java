package com.amazonaws.saas.eks.cashdrawer.dto.responses.workstations;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class WorkstationResponse {
    private String id;
    private String number;
    private String name;
    private String ipAddress;
    private String hsn;
    private Date created;
    private Date modified;
}

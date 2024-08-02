package com.amazonaws.saas.eks.cashdrawer.dto.responses.workstations;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListWorkstationsResponse {
    private List<WorkstationResponse> workstations;
    private long count;
}

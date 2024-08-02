package com.amazonaws.saas.eks.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
public class SalesTaxReportRequest {
    @NotNull
    private ZonedDateTime fromDate;
    private ZonedDateTime toDate;
    private Integer from;
    private Integer size;
}

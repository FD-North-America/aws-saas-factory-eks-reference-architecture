package com.amazonaws.saas.eks.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
public class PaidOutCodesAndDiscountsReportRequest {
    @NotNull
    private ZonedDateTime fromDate;
    private ZonedDateTime toDate;
}

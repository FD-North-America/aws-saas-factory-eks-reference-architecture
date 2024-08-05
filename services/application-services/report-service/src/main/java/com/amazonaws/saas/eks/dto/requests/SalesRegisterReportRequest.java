package com.amazonaws.saas.eks.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
public class SalesRegisterReportRequest {
    @NotNull
    private ZonedDateTime invoiceFromDate;
    private ZonedDateTime invoiceToDate;
    private String invoiceNumberFrom;
    private String invoiceNumberTo;
    private String salesRep;
    private Integer from;
    private Integer size;
}

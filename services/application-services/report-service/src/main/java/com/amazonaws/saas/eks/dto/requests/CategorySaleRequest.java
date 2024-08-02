package com.amazonaws.saas.eks.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class CategorySaleRequest {
    @NotNull
    private ZonedDateTime fromDate;
    private ZonedDateTime toDate;
    private String fromCategoryNumber;
    private String toCategoryNumber;
    private String fromProductNumber;
    private String toProductNumber;
    private Boolean displayCost;
    private Boolean displayProfitMargin;
}

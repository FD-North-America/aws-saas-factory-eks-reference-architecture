package com.amazonaws.saas.eks.dto.responses.returneditems;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReturnedItemsReportResponse {
    private List<ReturnedItem> returnedItems = new ArrayList<>();

    private BigDecimal total = BigDecimal.ZERO;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String salesRep;
}

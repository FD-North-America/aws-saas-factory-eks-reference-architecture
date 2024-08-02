package com.amazonaws.saas.eks.order.dto.requests.tax;

import com.amazonaws.saas.eks.order.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.order.model.enums.TaxType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UpdateTaxRequest {
    @ValueOfEnum(enumClass = TaxType.class)
    private String type;

    private String exemptCode;

    private String certificateId;

    private String streetAddress;

    private String city;

    private String county;

    private String state;

    private String zip;
}

package com.amazonaws.saas.eks.product.dto.responses.uom;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UOMResponse {
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Double factor;

    @Getter
    @Setter
    private String barcode;

    @Getter
    @Setter
    private String alternateId;

    @Getter
    @Setter
    private Date created;

    @Getter
    @Setter
    private Date modified;
}

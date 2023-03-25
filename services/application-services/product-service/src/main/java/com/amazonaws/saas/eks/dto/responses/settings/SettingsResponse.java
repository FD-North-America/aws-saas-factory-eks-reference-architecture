package com.amazonaws.saas.eks.dto.responses.settings;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettingsResponse {
    @Getter
    @Setter
    private List<String> unitOfMeasureNames;

    @Getter
    @Setter
    private Date created;

    @Getter
    @Setter
    private Date modified;
}

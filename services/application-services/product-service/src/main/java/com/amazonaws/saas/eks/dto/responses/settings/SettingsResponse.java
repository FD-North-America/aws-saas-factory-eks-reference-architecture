package com.amazonaws.saas.eks.dto.responses.settings;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettingsResponse {
    private List<String> unitOfMeasureNames;
    private String printerIp;
    private Date created;
    private Date modified;
}

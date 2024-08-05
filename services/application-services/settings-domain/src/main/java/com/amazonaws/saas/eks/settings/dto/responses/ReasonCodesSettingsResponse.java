package com.amazonaws.saas.eks.settings.dto.responses;

import com.amazonaws.saas.eks.settings.dto.requests.reasoncodes.ReasonCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ReasonCodesSettingsResponse {
    private List<ReasonCode> reasonCodes;

    private Date created;

    private Date modified;
}

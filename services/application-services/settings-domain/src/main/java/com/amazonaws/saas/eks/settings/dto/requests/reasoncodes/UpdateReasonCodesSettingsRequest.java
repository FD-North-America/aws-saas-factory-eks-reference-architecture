package com.amazonaws.saas.eks.settings.dto.requests.reasoncodes;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
public class UpdateReasonCodesSettingsRequest {
    private List<@Valid ReasonCode> reasonCodes;
}

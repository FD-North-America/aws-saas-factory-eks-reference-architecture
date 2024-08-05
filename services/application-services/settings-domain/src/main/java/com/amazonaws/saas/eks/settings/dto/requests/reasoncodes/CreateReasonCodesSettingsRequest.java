package com.amazonaws.saas.eks.settings.dto.requests.reasoncodes;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class CreateReasonCodesSettingsRequest {
    @NotNull
    private List<@Valid ReasonCode> reasonCodes;
}

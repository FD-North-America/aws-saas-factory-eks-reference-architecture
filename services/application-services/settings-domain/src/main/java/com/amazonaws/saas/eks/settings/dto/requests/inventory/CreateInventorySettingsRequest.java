package com.amazonaws.saas.eks.settings.dto.requests.inventory;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class CreateInventorySettingsRequest {
    @NotNull
    private @Valid OrderNumberSequence orderNumberSequence;

    @NotEmpty
    private List<@Valid Uom> unitsOfMeasure;

    @NotEmpty
    private List<@Valid StoreLocationCode> storeLocationCodes;
}

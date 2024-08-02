package com.amazonaws.saas.eks.settings.dto.requests.inventory;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
public class UpdateInventorySettingsRequest {
    private @Valid OrderNumberSequence orderNumberSequence;

    private List<@Valid Uom> unitsOfMeasure;

    private List<@Valid StoreLocationCode> storeLocationCodes;
}

package com.amazonaws.saas.eks.settings.dto.responses;

import com.amazonaws.saas.eks.settings.model.v2.inventory.OrderNumberSequence;
import com.amazonaws.saas.eks.settings.model.v2.inventory.StoreLocationCode;
import com.amazonaws.saas.eks.settings.model.v2.inventory.Uom;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class InventorySettingsResponse {
    private OrderNumberSequence orderNumberSequence;

    private List<Uom> unitsOfMeasure;

    private List<StoreLocationCode> storeLocationCodes;

    private Date created;

    private Date modified;
}

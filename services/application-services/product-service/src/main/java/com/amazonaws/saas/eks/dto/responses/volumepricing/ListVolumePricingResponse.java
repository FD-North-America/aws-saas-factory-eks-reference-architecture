package com.amazonaws.saas.eks.dto.responses.volumepricing;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ListVolumePricingResponse {
    @Getter
    @Setter
    private List<VolumePricingResponse> volumePricingList = new ArrayList<>();

    public int getCount() {
        return volumePricingList.size();
    }
}

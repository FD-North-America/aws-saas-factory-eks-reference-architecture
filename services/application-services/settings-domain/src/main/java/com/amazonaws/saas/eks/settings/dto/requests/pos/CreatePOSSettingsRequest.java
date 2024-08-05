package com.amazonaws.saas.eks.settings.dto.requests.pos;


import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class CreatePOSSettingsRequest {
    @NotEmpty
    private List<@Valid SequenceNumber> sequenceNumbers;

    @NotNull
    private DisallowCashReceiptOptions disallowCashReceiptOptions;

    @NotNull
    private PickingTicketPrintOptions pickingTicketPrintOptions;
}

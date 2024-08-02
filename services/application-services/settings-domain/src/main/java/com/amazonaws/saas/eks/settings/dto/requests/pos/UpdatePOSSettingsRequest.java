package com.amazonaws.saas.eks.settings.dto.requests.pos;


import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
public class UpdatePOSSettingsRequest {
    private List<@Valid SequenceNumber> sequenceNumbers;

    private DisallowCashReceiptOptions disallowCashReceiptOptions;

    private PickingTicketPrintOptions pickingTicketPrintOptions;
}

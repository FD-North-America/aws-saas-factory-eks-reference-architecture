package com.amazonaws.saas.eks.settings.dto.responses;

import com.amazonaws.saas.eks.settings.model.v2.pos.DisallowCashReceiptOptions;
import com.amazonaws.saas.eks.settings.model.v2.pos.PickingTicketPrintOptions;
import com.amazonaws.saas.eks.settings.model.v2.pos.SequenceNumber;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class POSSettingsResponse {

    private List<SequenceNumber> sequenceNumbers;

    private DisallowCashReceiptOptions disallowCashReceiptOptions;

    private PickingTicketPrintOptions pickingTicketPrintOptions;

    private Date created;

    private Date modified;
}

package com.amazonaws.saas.eks.settings.dto.requests.pos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PickingTicketPrintOptions {
    private boolean withInvoice;
    private boolean withSalesOrder;
}

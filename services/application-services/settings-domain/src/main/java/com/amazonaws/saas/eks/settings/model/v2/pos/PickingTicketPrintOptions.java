package com.amazonaws.saas.eks.settings.model.v2.pos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PickingTicketPrintOptions {
    private Boolean withInvoice;
    private Boolean withSalesOrder;
}

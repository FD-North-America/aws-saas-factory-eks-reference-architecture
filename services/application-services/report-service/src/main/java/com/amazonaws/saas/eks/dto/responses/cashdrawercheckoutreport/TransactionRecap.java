package com.amazonaws.saas.eks.dto.responses.cashdrawercheckoutreport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRecap {
    private TransactionRecapAmounts cash = new TransactionRecapAmounts();

    private TransactionRecapAmounts card = new TransactionRecapAmounts();

    private TransactionRecapAmounts total = new TransactionRecapAmounts();

    private CardTotals cardTotals = new CardTotals();
}

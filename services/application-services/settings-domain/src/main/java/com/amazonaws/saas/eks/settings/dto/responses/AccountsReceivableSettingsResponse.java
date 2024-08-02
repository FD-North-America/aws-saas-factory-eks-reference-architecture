package com.amazonaws.saas.eks.settings.dto.responses;

import com.amazonaws.saas.eks.settings.model.v2.accountsreceivable.CustomerType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AccountsReceivableSettingsResponse {
    private List<CustomerType> customerTypes;

    private Date created;

    private Date modified;
}

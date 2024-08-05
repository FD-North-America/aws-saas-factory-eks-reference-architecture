package com.amazonaws.saas.eks.settings.dto.requests.accountsreceivable;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
public class UpdateAccountsReceivableSettingsRequest {
    private List<@Valid CustomerType> customerTypes;
}

package com.amazonaws.saas.eks.settings.dto.requests.accountsreceivable;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class CreateAccountsReceivableSettingsRequest {
    @NotEmpty
    private List<@Valid CustomerType> customerTypes;
}

package com.amazonaws.saas.eks.settings.mapper;

import com.amazonaws.saas.eks.settings.dto.requests.accountsreceivable.CreateAccountsReceivableSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.accountsreceivable.UpdateAccountsReceivableSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.AccountsReceivableSettingsResponse;
import com.amazonaws.saas.eks.settings.model.v2.accountsreceivable.AccountsReceivableSettings;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountsReceivableSettingsMapper {
    AccountsReceivableSettingsMapper INSTANCE = Mappers.getMapper(AccountsReceivableSettingsMapper.class);

    AccountsReceivableSettings createARSettingsRequestToARSettings(CreateAccountsReceivableSettingsRequest request);

    AccountsReceivableSettingsResponse arSettingsToARSettingsResponse(AccountsReceivableSettings settings);

    AccountsReceivableSettings updateARSettingsRequestToARSettings(UpdateAccountsReceivableSettingsRequest request);
}

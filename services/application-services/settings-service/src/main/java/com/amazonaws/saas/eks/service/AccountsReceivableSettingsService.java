package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.settings.dto.requests.accountsreceivable.CreateAccountsReceivableSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.accountsreceivable.UpdateAccountsReceivableSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.AccountsReceivableSettingsResponse;

public interface AccountsReceivableSettingsService {
    AccountsReceivableSettingsResponse create(String tenantId, CreateAccountsReceivableSettingsRequest request);

    AccountsReceivableSettingsResponse get(String tenantId);

    AccountsReceivableSettingsResponse update(String tenantId, UpdateAccountsReceivableSettingsRequest request);

    void delete(String tenantId);
}

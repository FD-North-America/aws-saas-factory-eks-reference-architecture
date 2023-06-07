package com.amazonaws.saas.eks.mapper;

import com.amazonaws.saas.eks.dto.requests.cashdrawers.CashDrawerTrayRequest;
import com.amazonaws.saas.eks.dto.requests.cashdrawers.CreateCashDrawerRequest;
import com.amazonaws.saas.eks.dto.requests.cashdrawers.UpdateCashDrawerRequest;
import com.amazonaws.saas.eks.dto.responses.cashdrawers.CashDrawerResponse;
import com.amazonaws.saas.eks.dto.responses.cashdrawers.CashDrawerTrayResponse;
import com.amazonaws.saas.eks.dto.responses.cashdrawers.checkout.ListCashDrawerAdminResponse;
import com.amazonaws.saas.eks.dto.responses.cashdrawers.ListCashDrawersResponse;
import com.amazonaws.saas.eks.model.CashDrawer;
import com.amazonaws.saas.eks.model.CashDrawerSearchResponse;
import com.amazonaws.saas.eks.model.CashDrawerTray;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CashDrawerMapper {
    CashDrawerMapper INSTANCE = Mappers.getMapper(CashDrawerMapper.class);

    CashDrawer createCashDrawerRequestToCashDrawer(CreateCashDrawerRequest request);

    CashDrawerResponse cashDrawerToCashDrawerResponse(CashDrawer cashDrawer);

    CashDrawer updateCashDrawerRequestToCashDrawer(UpdateCashDrawerRequest request);

    CashDrawerTray cashDrawerTrayRequestToCashDrawerTray(CashDrawerTrayRequest request);

    CashDrawerTrayResponse cashDrawerTrayToCashDrawerTrayResponse(CashDrawerTray tray);

    ListCashDrawersResponse cashDrawerSearchResponseToListResponse(CashDrawerSearchResponse searchResponse);

    ListCashDrawerAdminResponse cashDrawerSearchResponseToAdminListResponse(CashDrawerSearchResponse searchResponse);
}

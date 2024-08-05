package com.amazonaws.saas.eks.cashdrawer.mapper;

import com.amazonaws.saas.eks.cashdrawer.dto.requests.CashDrawerTrayRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.CreateCashDrawerRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.UpdateCashDrawerRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.CashDrawerResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.CashDrawerTrayResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.ListCashDrawersResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.CheckoutDetailsResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.CheckoutResponse;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawer;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerCheckout;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerSearchResponse;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerTray;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CashDrawerMapper {
    CashDrawerMapper INSTANCE = Mappers.getMapper(CashDrawerMapper.class);

    CashDrawer createCashDrawerRequestToCashDrawer(CreateCashDrawerRequest request);

    CashDrawerResponse cashDrawerToCashDrawerResponse(CashDrawer cashDrawer);

    CashDrawer updateCashDrawerRequestToCashDrawer(UpdateCashDrawerRequest request);

    CashDrawerTray cashDrawerTrayRequestToCashDrawerTray(CashDrawerTrayRequest request);

    CashDrawerTrayResponse cashDrawerTrayToCashDrawerTrayResponse(CashDrawerTray tray);

    ListCashDrawersResponse cashDrawerSearchResponseToListResponse(CashDrawerSearchResponse searchResponse);

    List<CashDrawerResponse> cashDrawersToCashDrawerResponses(List<CashDrawer> cashDrawers);

    List<CheckoutResponse> cashDrawerCheckoutsToCheckoutResponse(List<CashDrawerCheckout> cashDrawerCheckouts);

    List<CashDrawerTrayResponse> cashDrawerTrayListToCashDrawerTrayListResponse(List<CashDrawerTray> cashDrawerTrays);

    CheckoutDetailsResponse cashDrawerCheckoutToCheckoutDetailsResponse(CashDrawerCheckout checkout);
}

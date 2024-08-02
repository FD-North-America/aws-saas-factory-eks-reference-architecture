package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.cashdrawer.dto.requests.CreateCashDrawerRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.ListCashDrawersRequestParams;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.UpdateCashDrawerRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.CashDrawerResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.ListCashDrawersResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.CheckoutDetailsResponse;
import com.amazonaws.saas.eks.cashdrawer.mapper.CashDrawerMapper;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawer;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerSearchResponse;
import com.amazonaws.saas.eks.cashdrawer.model.enums.CashDrawerStatus;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.repository.CashDrawerRepository;
import com.amazonaws.saas.eks.repository.OrderRepository;
import com.amazonaws.saas.eks.service.CashDrawerService;
import com.amazonaws.saas.eks.service.CheckoutService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.amazonaws.saas.eks.util.Utils.getTransactionDetails;
import static com.amazonaws.saas.eks.util.Utils.getTransactionTotals;

@Service
public class CashDrawerServiceImpl implements CashDrawerService {
    private static final Logger logger = LogManager.getLogger(CashDrawerServiceImpl.class);

    private static final int DEFAULT_SEARCH_START = 0;
    private static final int DEFAULT_SEARCH_SIZE = 10;

    private final CashDrawerRepository cashDrawerRepository;

    private final OrderRepository orderRepository;

    private final CheckoutService checkoutService;

    public CashDrawerServiceImpl(CashDrawerRepository cashDrawerRepository,
                                 OrderRepository orderRepository,
                                 CheckoutService checkoutService) {
        this.cashDrawerRepository = cashDrawerRepository;
        this.orderRepository = orderRepository;
        this.checkoutService = checkoutService;
    }

    @Override
    public CashDrawerResponse create(CreateCashDrawerRequest request, String tenantId) {
        CashDrawer cashDrawer = CashDrawerMapper.INSTANCE.createCashDrawerRequestToCashDrawer(request);
        CashDrawer model = cashDrawerRepository.create(cashDrawer, tenantId);
        return CashDrawerMapper.INSTANCE.cashDrawerToCashDrawerResponse(model);
    }

    @Override
    public CashDrawerResponse get(String cashDrawerId, String tenantId) {
        CashDrawer cashDrawer = cashDrawerRepository.get(cashDrawerId, tenantId);
        return CashDrawerMapper.INSTANCE.cashDrawerToCashDrawerResponse(cashDrawer);
    }

    @Override
    public CashDrawerResponse update(String cashDrawerId, UpdateCashDrawerRequest request, String tenantId) {
        CashDrawer oldCashDrawer = cashDrawerRepository.get(cashDrawerId, tenantId);

        CashDrawer newCashDrawer = CashDrawerMapper.INSTANCE.updateCashDrawerRequestToCashDrawer(request);
        newCashDrawer.setId(cashDrawerId);

        // Update fields based on updated request status
        newCashDrawer = checkoutService.process(tenantId, request.getStatus(), oldCashDrawer, newCashDrawer);

        CashDrawer updatedCashDrawer = cashDrawerRepository.update(newCashDrawer, tenantId);

        return CashDrawerMapper.INSTANCE.cashDrawerToCashDrawerResponse(updatedCashDrawer);
    }

    @Override
    public void delete(String cashDrawerId, String tenantId) {
        cashDrawerRepository.delete(cashDrawerId, tenantId);
    }

    @Override
    public ListCashDrawersResponse getAll(ListCashDrawersRequestParams params, String tenantId) {
        CashDrawerSearchResponse searchResponse = search(params, tenantId);
        return CashDrawerMapper.INSTANCE.cashDrawerSearchResponseToListResponse(searchResponse);
    }

    @Override
    public CheckoutDetailsResponse getCheckoutDetails(String cashDrawerId, String tenantId) {
        CashDrawer cashDrawer = cashDrawerRepository.get(cashDrawerId, tenantId);
        List<Order> orders = orderRepository.getOrdersByCashDrawer(tenantId, cashDrawerId, cashDrawer.getStartupDate(), new Date());
        CheckoutDetailsResponse response = new CheckoutDetailsResponse();
        response.setTransactionDetails(getTransactionDetails(orders));
        response.setTransactionTotals(getTransactionTotals(orders));
        response.setCardTotalAmount(cashDrawer.getCardTotalAmount());
        response.setCashTotalAmount(cashDrawer.getCashTotalAmount());
        return response;
    }

    @Override
    public ListCashDrawersResponse getByAssignedUser(String username, String tenantId) {
        ListCashDrawersResponse response = new ListCashDrawersResponse();
        List<CashDrawer> cashDrawers = cashDrawerRepository.getByAssignedUser(tenantId, username);
        response.setCashDrawers(CashDrawerMapper.INSTANCE.cashDrawersToCashDrawerResponses(cashDrawers));
        return response;
    }

    private CashDrawerSearchResponse search(ListCashDrawersRequestParams params, String tenantId) {
        int from = params.getFrom() == null ? DEFAULT_SEARCH_START : params.getFrom();
        int size = params.getSize() == null ? DEFAULT_SEARCH_SIZE : params.getSize();
        return cashDrawerRepository.search(tenantId, from, size,
                params.getFilter(), params.getSortBy());
    }
}

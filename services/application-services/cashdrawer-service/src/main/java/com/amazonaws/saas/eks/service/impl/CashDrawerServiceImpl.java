package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.cashdrawer.dto.requests.CreateCashDrawerRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.ListCashDrawersRequestParams;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.UpdateCashDrawerRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.CashDrawerResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.ListCashDrawersResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.CheckoutDetailsResponse;
import com.amazonaws.saas.eks.cashdrawer.mapper.CashDrawerMapper;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawer;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerCheckout;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerSearchResponse;
import com.amazonaws.saas.eks.cashdrawer.model.enums.CashDrawerStatus;
import com.amazonaws.saas.eks.exception.CashDrawerException;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.repository.CashDrawerCheckoutRepository;
import com.amazonaws.saas.eks.repository.CashDrawerRepository;
import com.amazonaws.saas.eks.repository.OrderRepository;
import com.amazonaws.saas.eks.service.CashDrawerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.amazonaws.saas.eks.util.Utils.getTransactionDetails;
import static com.amazonaws.saas.eks.util.Utils.getTransactionTotals;

@Service
public class CashDrawerServiceImpl implements CashDrawerService {
    private static final Logger logger = LogManager.getLogger(CashDrawerServiceImpl.class);

    private static final int DEFAULT_SEARCH_START = 0;
    private static final int DEFAULT_SEARCH_SIZE = 10;

    @Autowired
    private CashDrawerRepository cashDrawerRepository;

    @Autowired
    private CashDrawerCheckoutRepository cashDrawerCheckoutRepository;

    @Autowired
    private OrderRepository orderRepository;

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

        String status = request.getStatus();
        if (status != null && status.equals(CashDrawerStatus.ACTIVE.toString())
                && oldCashDrawer.getStatus().equals(CashDrawerStatus.CHECKED.toString())) {
            newCashDrawer.setClearedDate(new Date());
            newCashDrawer.setClearedBy(newCashDrawer.getAssignedUser());

            CashDrawerCheckout checkout = cashDrawerCheckoutRepository.getByCashDrawerId(cashDrawerId, tenantId);
            if (checkout == null) {
                String message = String.format("TenantId: %s-Cannot clear a cash drawer that isn't checked out", tenantId);
                logger.error(message);
                throw new CashDrawerException(message);
            }

            checkout.setStatus(CashDrawerStatus.CLEARED.toString());
            checkout.setClearedDate(newCashDrawer.getClearedDate());
            checkout.setClearedBy(newCashDrawer.getAssignedUser());
            checkout.setTrays(newCashDrawer.getTrays());
            checkout.setTraysTotalAmount(newCashDrawer.getTraysTotalAmount());
            checkout.setCashTotalAmount(newCashDrawer.getCashTotalAmount());
            checkout.setCardTotalAmount(newCashDrawer.getCardTotalAmount());
            cashDrawerCheckoutRepository.update(checkout, tenantId);

            newCashDrawer.setTrays(new ArrayList<>());
            newCashDrawer.setTraysTotalAmount(BigDecimal.ZERO);
            newCashDrawer.setCardTotalAmount(BigDecimal.ZERO);
            newCashDrawer.setCashTotalAmount(BigDecimal.ZERO);
        }

        if (status != null && status.equals(CashDrawerStatus.CHECKED.toString())) {
            if (!StringUtils.hasLength(newCashDrawer.getCheckoutRep())) {
                String message = String.format("TenantId: %s-CheckoutRep user must be defined ", tenantId);
                logger.error(message);
                throw new CashDrawerException(message);
            }
            List<Order> orders = orderRepository.getOrdersByCashDrawer(tenantId, cashDrawerId, oldCashDrawer.getStartupDate(), new Date());
            BigDecimal checkoutAmount = orders.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            newCashDrawer.setCheckoutAmounts(checkoutAmount);

            CashDrawerCheckout checkout = new CashDrawerCheckout();
            checkout.setCashDrawerId(cashDrawerId);
            checkout.setCashDrawerNumber(oldCashDrawer.getNumber());
            checkout.setCheckoutRep(newCashDrawer.getCheckoutRep());
            checkout.setCheckoutAmounts(newCashDrawer.getCheckoutAmounts());
            checkout.setStartupDate(oldCashDrawer.getStartupDate());
            checkout.setStartUpAmount(oldCashDrawer.getStartUpAmount());

            cashDrawerCheckoutRepository.create(checkout, tenantId);

            newCashDrawer.setCheckoutDate(checkout.getCreated());
        }

        if (status != null && status.equals(CashDrawerStatus.CLEARED.toString())) {
            if (!StringUtils.hasLength(newCashDrawer.getClearedBy())) {
                String message = String.format("TenantId: %s-ClearedBy user must be defined ", tenantId);
                logger.error(message);
                throw new CashDrawerException(message);
            }
            newCashDrawer.setClearedDate(new Date());

            CashDrawerCheckout checkout = cashDrawerCheckoutRepository.getByCashDrawerId(cashDrawerId, tenantId);
            if (checkout == null) {
                String message = String.format("TenantId: %s-Cannot clear a cash drawer that isn't checked out", tenantId);
                logger.error(message);
                throw new CashDrawerException(message);
            }
            checkout.setStatus(CashDrawerStatus.CLEARED.toString());
            checkout.setClearedDate(newCashDrawer.getClearedDate());
            checkout.setClearedBy(newCashDrawer.getClearedBy());
            cashDrawerCheckoutRepository.update(checkout, tenantId);
        }

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

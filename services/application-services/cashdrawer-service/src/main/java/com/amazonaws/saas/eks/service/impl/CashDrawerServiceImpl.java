package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.dto.requests.cashdrawers.CreateCashDrawerRequest;
import com.amazonaws.saas.eks.dto.requests.cashdrawers.ListCashDrawersRequestParams;
import com.amazonaws.saas.eks.dto.requests.cashdrawers.UpdateCashDrawerRequest;
import com.amazonaws.saas.eks.dto.responses.TransactionResponse;
import com.amazonaws.saas.eks.dto.responses.cashdrawers.*;
import com.amazonaws.saas.eks.dto.responses.cashdrawers.checkout.*;
import com.amazonaws.saas.eks.exception.CashDrawerException;
import com.amazonaws.saas.eks.mapper.CashDrawerMapper;
import com.amazonaws.saas.eks.mapper.OrderMapper;
import com.amazonaws.saas.eks.model.*;
import com.amazonaws.saas.eks.model.enums.CashDrawerStatus;
import com.amazonaws.saas.eks.model.enums.PaymentType;
import com.amazonaws.saas.eks.repository.CashDrawerCheckoutRepository;
import com.amazonaws.saas.eks.repository.CashDrawerRepository;
import com.amazonaws.saas.eks.repository.OrderRepository;
import com.amazonaws.saas.eks.service.CashDrawerService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class CashDrawerServiceImpl implements CashDrawerService {

    private static final int DEFAULT_SEARCH_START = 0;
    private static final int DEFAULT_SEARCH_SIZE =10;

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
        CashDrawer cashDrawer = CashDrawerMapper.INSTANCE.updateCashDrawerRequestToCashDrawer(request);
        cashDrawer.setId(cashDrawerId);

        CashDrawer updatedCashDrawer = cashDrawerRepository.update(cashDrawer, tenantId);

        String status = request.getStatus();
        if (status.equals(CashDrawerStatus.CHECKED.toString())) {
            CashDrawerCheckout checkout = new CashDrawerCheckout();
            checkout.setCashDrawerId(cashDrawerId);
            checkout.setCheckoutRep(cashDrawer.getCheckoutRep());
            checkout.setCheckoutAmounts(updatedCashDrawer.getCheckoutAmounts());
            checkout.setStartupDate(cashDrawer.getStartupDate());
            cashDrawerCheckoutRepository.create(checkout, tenantId);
        }

        if (status.equals(CashDrawerStatus.CLEARED.toString())) {
            cashDrawer.setClearedDate(new Date());
            CashDrawerCheckout checkout = cashDrawerCheckoutRepository.getByCashDrawerId(cashDrawerId, tenantId);
            if (checkout == null) {
                throw new CashDrawerException("Cannot clear a cash drawer that isn't checked out");
            }
            checkout.setStatus(CashDrawerStatus.CLEARED.toString());
            checkout.setClearedDate(cashDrawer.getClearedDate());
            checkout.setClearedBy(cashDrawer.getClearedBy());
            cashDrawerCheckoutRepository.update(checkout, tenantId);
        }

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
    public ListCashDrawerAdminResponse getAllAdmin(ListCashDrawersRequestParams params, String tenantId) {
        CashDrawerSearchResponse searchResponse = search(params, tenantId);
        return CashDrawerMapper.INSTANCE.cashDrawerSearchResponseToAdminListResponse(searchResponse);
    }

    @Override
    public CheckoutDetailsResponse getCheckoutDetails(String cashDrawerId, String tenantId) {
        CheckoutDetailsResponse response = new CheckoutDetailsResponse();
        List<Order> orders = orderRepository.getOrdersByCashDrawer(cashDrawerId, tenantId);
        response.setTransactionDetails(getTransactionDetails(orders));
        response.setTransactionTotals(getTransactionTotals(orders));
        return response;
    }

    private CheckoutTransactionDetailsResponse getTransactionDetails(List<Order> orders) {
        CheckoutTransactionDetailsResponse response = new CheckoutTransactionDetailsResponse();
        long transactionCount = 0;
        for (Order o : orders) {
            for (Transaction t : o.getTransactions()) {
                TransactionResponse tResponse = OrderMapper.INSTANCE.transactionToTransactionResponse(t);
                response.getTransactions().add(tResponse);
                transactionCount++;
            }
        }
        response.setCount(transactionCount);
        return response;
    }

    private CheckoutTransactionTotalResponse getTransactionTotals(List<Order> orders) {
        CheckoutTransactionTotalResponse response = new CheckoutTransactionTotalResponse();
        CheckoutTotalResponse cash = new CheckoutTotalResponse();
        CheckoutTotalResponse card = new CheckoutTotalResponse();
        CheckoutTotalResponse check = new CheckoutTotalResponse();
        CheckoutTotalResponse total = new CheckoutTotalResponse();
        for (Order o : orders) {
            for (Transaction t : o.getTransactions()) {
                if (t.getType().equals(PaymentType.CASH.toString())) {
                    cash.setSales(cash.getSales().add(t.getAmount()));
                } else if (t.getType().equals(PaymentType.CARD.toString())) {
                    card.setSales(card.getSales().add(t.getAmount()));
                } else if (t.getType().equals(PaymentType.CHECK.toString())) {
                    check.setSales(check.getSales().add(t.getAmount()));
                }
            }
        }
        total.setSales(cash.getSales().add(card.getSales()).add(check.getSales()));

        response.setCash(cash);
        response.setCard(card);
        response.setCheck(check);
        response.setTotal(total);
        return response;
    }

    private CashDrawerSearchResponse search(ListCashDrawersRequestParams params, String tenantId) {
        int from = params.getFrom() == null ? DEFAULT_SEARCH_START : params.getFrom();
        int size = params.getSize() == null ? DEFAULT_SEARCH_SIZE : params.getSize();
        return cashDrawerRepository.search(tenantId, from, size,
                params.getFilter(), params.getSortBy());
    }
}

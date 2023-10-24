package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.CheckoutDetailsResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.ListCheckoutResponse;
import com.amazonaws.saas.eks.cashdrawer.mapper.CashDrawerMapper;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerCheckout;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerCheckoutSearchResponse;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.repository.CashDrawerCheckoutRepository;
import com.amazonaws.saas.eks.repository.OrderRepository;
import com.amazonaws.saas.eks.service.CheckoutService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.amazonaws.saas.eks.util.Utils.getTransactionDetails;
import static com.amazonaws.saas.eks.util.Utils.getTransactionTotals;

@Service
public class CheckoutServiceImpl implements CheckoutService {
    private static final Logger logger = LogManager.getLogger(CheckoutServiceImpl.class);

    @Autowired
    private CashDrawerCheckoutRepository checkoutRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public ListCheckoutResponse get(String tenantId, String username) {
        ListCheckoutResponse response = new ListCheckoutResponse();
        CashDrawerCheckoutSearchResponse searchResponse = checkoutRepository.get(tenantId, username);
        response.setCheckouts(CashDrawerMapper.INSTANCE.cashDrawerCheckoutsToCheckoutResponse(searchResponse.getCheckouts()));
        response.setCount(searchResponse.getCount());
        return response;
    }

    @Override
    public CheckoutDetailsResponse getById(String tenantId, String checkoutId) {
        CashDrawerCheckout checkout = checkoutRepository.getById(tenantId, checkoutId);
        List<Order> orders = orderRepository.getOrdersByCashDrawer(tenantId, checkout.getCashDrawerId(),
                checkout.getStartupDate(), checkout.getModified());
        CheckoutDetailsResponse response = new CheckoutDetailsResponse();
        response.setStartUpAmount(checkout.getStartUpAmount());
        response.setTrays(CashDrawerMapper.INSTANCE.cashDrawerTrayListToCashDrawerTrayListResponse(checkout.getTrays()));
        response.setTraysTotalAmount(checkout.getTraysTotalAmount());
        response.setTransactionDetails(getTransactionDetails(orders));
        response.setTransactionTotals(getTransactionTotals(orders));
        return response;
    }
}

package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.CheckoutDetailsResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.ListCheckoutResponse;
import com.amazonaws.saas.eks.cashdrawer.mapper.CashDrawerMapper;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawer;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerCheckout;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerCheckoutSearchResponse;
import com.amazonaws.saas.eks.cashdrawer.model.enums.CashDrawerStatus;
import com.amazonaws.saas.eks.exception.CashDrawerException;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.repository.CashDrawerCheckoutRepository;
import com.amazonaws.saas.eks.repository.OrderRepository;
import com.amazonaws.saas.eks.service.CheckoutService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.amazonaws.saas.eks.util.Utils.getTransactionDetails;
import static com.amazonaws.saas.eks.util.Utils.getTransactionTotals;

@Service
public class CheckoutServiceImpl implements CheckoutService {
    private static final Logger logger = LogManager.getLogger(CheckoutServiceImpl.class);

    private final CashDrawerCheckoutRepository checkoutRepository;

    private final OrderRepository orderRepository;

    public CheckoutServiceImpl(CashDrawerCheckoutRepository repository, OrderRepository orderRepository) {
        this.checkoutRepository = repository;
        this.orderRepository = orderRepository;
    }

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
        CheckoutDetailsResponse response = CashDrawerMapper.INSTANCE.cashDrawerCheckoutToCheckoutDetailsResponse(checkout);
        response.setTransactionDetails(getTransactionDetails(orders));
        response.setTransactionTotals(getTransactionTotals(orders));
        return response;
    }

    /**
     * Process the checkout request
     *
     * @param tenantId          tenant id
     * @param status            status
     * @param oldCashDrawer     {@link CashDrawer}
     * @param newCashDrawer     {@link CashDrawer}
     * @return {@link CashDrawer}
     */
    @Override
    public CashDrawer process(String tenantId,
                              String status,
                              CashDrawer oldCashDrawer,
                              CashDrawer newCashDrawer) {
        if (status != null && status.equals(CashDrawerStatus.ACTIVE.toString())
                && oldCashDrawer.getStatus().equals(CashDrawerStatus.CHECKED.toString())) {
            return resetCashDrawer(tenantId, newCashDrawer);
        }

        if (status != null && status.equals(CashDrawerStatus.CHECKED.toString())) {
            return checkCashDrawer(tenantId, oldCashDrawer, newCashDrawer);
        }

        if (status != null && status.equals(CashDrawerStatus.CLEARED.toString())) {
            return clearCashDrawer(tenantId, newCashDrawer);
        }

        // No Changes, return the newCashDrawer
        return newCashDrawer;
    }

    private CashDrawer clearCashDrawer(String tenantId, CashDrawer newCashDrawer) {
        if (!StringUtils.hasLength(newCashDrawer.getClearedBy())) {
            String message = String.format("TenantId: %s-ClearedBy user must be defined ", tenantId);
            logger.error(message);
            throw new CashDrawerException(message);
        }
        newCashDrawer.setClearedDate(new Date());

        CashDrawerCheckout checkout = checkoutRepository.getByCashDrawerId(newCashDrawer.getId(), tenantId);
        if (checkout == null) {
            String message = String.format("TenantId: %s-Cannot clear a cash drawer that isn't checked out", tenantId);
            logger.error(message);
            throw new CashDrawerException(message);
        }
        checkout.setStatus(CashDrawerStatus.CLEARED.toString());
        checkout.setClearedDate(newCashDrawer.getClearedDate());
        checkout.setClearedBy(newCashDrawer.getClearedBy());
        checkoutRepository.update(checkout, tenantId);

        return newCashDrawer;
    }

    private CashDrawer checkCashDrawer(String tenantId, CashDrawer oldCashDrawer, CashDrawer newCashDrawer) {
        if (!StringUtils.hasLength(newCashDrawer.getCheckoutRep())) {
            String message = String.format("TenantId: %s-CheckoutRep user must be defined ", tenantId);
            logger.error(message);
            throw new CashDrawerException(message);
        }
        List<Order> orders = orderRepository.getOrdersByCashDrawer(tenantId, newCashDrawer.getId(),
                oldCashDrawer.getStartupDate(), new Date());
        BigDecimal checkoutAmount = orders.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        newCashDrawer.setCheckoutAmounts(checkoutAmount);

        CashDrawerCheckout checkout = new CashDrawerCheckout();
        checkout.setCashDrawerId(newCashDrawer.getId());
        checkout.setCashDrawerNumber(oldCashDrawer.getNumber());
        checkout.setCheckoutRep(newCashDrawer.getCheckoutRep());
        checkout.setCheckoutAmounts(newCashDrawer.getCheckoutAmounts());
        checkout.setStartupDate(oldCashDrawer.getStartupDate());
        checkout.setStartUpAmount(oldCashDrawer.getStartUpAmount());

        checkoutRepository.create(checkout, tenantId);

        newCashDrawer.setCheckoutDate(checkout.getCreated());

        return newCashDrawer;
    }

    private CashDrawer resetCashDrawer(String tenantId, CashDrawer newCashDrawer) {
        newCashDrawer.setClearedDate(new Date());
        newCashDrawer.setClearedBy(newCashDrawer.getAssignedUser());
        CashDrawerCheckout checkout = checkoutRepository.getByCashDrawerId(newCashDrawer.getId(), tenantId);
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
        checkoutRepository.update(checkout, tenantId);

        newCashDrawer.setTrays(new ArrayList<>());
        newCashDrawer.setTraysTotalAmount(BigDecimal.ZERO);
        newCashDrawer.setCardTotalAmount(BigDecimal.ZERO);
        newCashDrawer.setCashTotalAmount(BigDecimal.ZERO);

        return newCashDrawer;
    }
}

package com.amazonaws.saas.eks.util;

import com.amazonaws.saas.eks.cashdrawer.dto.responses.TransactionResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.CheckoutTotalResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.CheckoutTransactionDetailsResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.CheckoutTransactionTotalResponse;
import com.amazonaws.saas.eks.mapper.ServiceMapper;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.order.model.Transaction;
import com.amazonaws.saas.eks.order.model.enums.PaymentType;
import com.amazonaws.saas.eks.order.model.enums.TransactionType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Utils {
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static String convertToQueryDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        return format.format(date);
    }

    public static CheckoutTransactionDetailsResponse getTransactionDetails(List<Order> orders) {
        CheckoutTransactionDetailsResponse response = new CheckoutTransactionDetailsResponse();
        long transactionCount = 0;
        for (Order o : orders) {
            for (Transaction t : o.getTransactions()) {
                TransactionResponse tResponse = ServiceMapper.INSTANCE.transactionToTransactionResponse(t);
                response.getTransactions().add(tResponse);
                transactionCount++;
            }
        }
        response.setCount(transactionCount);
        return response;
    }

    public static CheckoutTransactionTotalResponse getTransactionTotals(List<Order> orders) {
        CheckoutTransactionTotalResponse response = new CheckoutTransactionTotalResponse();
        CheckoutTotalResponse cash = new CheckoutTotalResponse();
        CheckoutTotalResponse card = new CheckoutTotalResponse();
        CheckoutTotalResponse check = new CheckoutTotalResponse();
        CheckoutTotalResponse total = new CheckoutTotalResponse();
        for (Order o : orders) {
            for (Transaction t : o.getTransactions()) {
                if (t.getPaymentType().equals(PaymentType.CASH.toString())) {
                    if (t.getType().equals(TransactionType.PAID_OUT.toString())) {
                        cash.setPaidOut(cash.getPaidOut().add(t.getAmount().abs()));
                    } else if (t.getType().equals(TransactionType.TENDERED.toString())) {
                        cash.setSales(cash.getSales().add(t.getAmount()));
                    } else if (t.getType().equals(TransactionType.CHANGE.toString())) {
                        cash.setSales(cash.getSales().subtract(t.getAmount()));
                    }
                } else if (t.getPaymentType().equals(PaymentType.CARD.toString())) {
                    if (t.getType().equals(TransactionType.TENDERED.toString())) {
                        card.setSales(card.getSales().add(t.getAmount()));
                    } else if (t.getType().equals(TransactionType.CHANGE.toString())) {
                        card.setSales(card.getSales().subtract(t.getAmount()));
                    }
                } else if (t.getPaymentType().equals(PaymentType.CHECK.toString())) {
                    if (t.getType().equals(TransactionType.TENDERED.toString())) {
                        check.setSales(check.getSales().add(t.getAmount()));
                    } else if (t.getType().equals(TransactionType.CHANGE.toString())) {
                        check.setSales(check.getSales().subtract(t.getAmount()));
                    }
                }
            }
        }
        cash.setBalance(cash.getSales().subtract(cash.getPaidOut()));
        total.setSales(cash.getSales().add(card.getSales()).add(check.getSales()));
        total.setPaidOut(cash.getPaidOut().add(card.getPaidOut()).add(check.getPaidOut()));
        total.setBalance(cash.getBalance().add(card.getBalance()).add(check.getBalance()));

        response.setCash(cash);
        response.setCard(card);
        response.setCheck(check);
        response.setTotal(total);
        return response;
    }
}

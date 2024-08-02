package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.exception.OrderException;
import com.amazonaws.saas.eks.exception.OrderPaymentFailure;
import com.amazonaws.saas.eks.order.model.*;
import com.amazonaws.saas.eks.order.model.enums.*;
import com.amazonaws.saas.eks.payment.dto.responses.AuthResponse;
import com.amazonaws.saas.eks.payment.dto.responses.OrderPaymentResponse;
import com.amazonaws.saas.eks.repository.*;
import com.amazonaws.saas.eks.service.TransactionService;
import com.amazonaws.saas.eks.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.amazonaws.saas.eks.utils.Utils.roundValue;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final String APPROVED_CODE = "A";

    private static void createNewCashTransaction(BigDecimal amount, Order order, boolean isPaidOut) {
        Transaction transaction = new Transaction();
        transaction.setId(String.valueOf(UUID.randomUUID()));
        transaction.setDate(new Date());
        if (isPaidOut) {
            transaction.setType(TransactionType.PAID_OUT.toString());
        } else {
            TransactionType type = amount.compareTo(BigDecimal.ZERO) < 0 ? TransactionType.CHANGE : TransactionType.TENDERED;
            transaction.setType(type.toString());
        }
        transaction.setPaymentType(PaymentType.CASH.toString());
        transaction.setAmount(roundValue(amount));
        order.getTransactions().add(transaction);
    }

    private static Transaction getTenderedCashTransaction(Order order) {
        return order.getTransactions()
                .stream()
                .filter(t -> t.getPaymentType().equals(PaymentType.CASH.toString())
                        && t.getType().equals(TransactionType.TENDERED.toString()))
                .findFirst()
                .orElse(null);
    }

    private static void updateSavedCashTransaction(BigDecimal amount, Transaction savedCashTransaction) {
        BigDecimal updatedTransactionAmount = roundValue(savedCashTransaction.getAmount().add(amount));
        savedCashTransaction.setAmount(updatedTransactionAmount);
    }

    private static void updateOrderCashPaymentAmount(Order order, BigDecimal amount) {
        BigDecimal updatedCashPaymentAmount = roundValue(order.getCashPaymentAmount().add(roundValue(amount)));
        order.setCashPaymentAmount(updatedCashPaymentAmount);
    }

    private static void createNewChangeTransaction(Order order, String parentCashTransactionId) {
        Transaction changeTransaction = new Transaction();
        changeTransaction.setDate(new Date());
        changeTransaction.setId(String.valueOf(UUID.randomUUID()));
        changeTransaction.setParentTransactionId(parentCashTransactionId);
        changeTransaction.setType(TransactionType.CHANGE.toString());
        changeTransaction.setPaymentType(PaymentType.CASH.toString());
        changeTransaction.setAmount(order.getBalanceDue().negate());
        order.getTransactions().add(changeTransaction);
    }

    private static Transaction getChangeTransaction(Order order) {
        return order.getTransactions()
                .stream()
                .filter(t -> t.getType().equals(TransactionType.CHANGE.toString()))
                .findFirst()
                .orElse(null);
    }

    private static Transaction buildCardTransaction(Date responseDate, AuthResponse authResponse) {
        Transaction transaction = new Transaction();
        transaction.setDate(responseDate);
        String token = authResponse.getToken();
        transaction.setPaymentType(PaymentType.CARD.toString());
        transaction.setType(TransactionType.TENDERED.toString());
        transaction.setRetRef(authResponse.getRetRef());
        transaction.setAmount(new BigDecimal(authResponse.getAmount()));
        transaction.setCcLastDigits(getLastDigits(token));
        transaction.setCcType(getCardType(token).toString());
        return transaction;
    }

    private static String getLastDigits(String token) {
        return token.substring(token.length() - 4);
    }

    private static CreditCardType getCardType(String token) {
        switch (token.charAt(1)) {
            case '3':
                return CreditCardType.AMERICAN_EXPRESS;
            case '4':
                return CreditCardType.VISA;
            case '5':
                return CreditCardType.MASTERCARD;
            case '6':
                return CreditCardType.DISCOVER;
            default:
                throw new OrderException("Invalid Credit Card Type");
        }
    }

    private static void handlePaymentFailure(OrderRepository repository, String tenantId, Order order, String errorMessage) {
        order.setFailedTransactionCount(order.getFailedTransactionCount() + 1);
        repository.save(order, tenantId);
        throw new OrderPaymentFailure(errorMessage);
    }

    /**
     * @inheritDoc
     */
    public void addCardPaymentToOrder(OrderRepository repository, String tenantId, Order order, List<OrderPaymentResponse> payments, Map<String, Transaction> transactionMap) {
        // Ordering by the most recent transactions
        payments.sort(Comparator.comparing(OrderPaymentResponse::getResponseDate).reversed());
        for (OrderPaymentResponse r : payments) {
            AuthResponse authResponse = r.getResponse();
            if (isFailedTransaction(r)) {
                String errorMessage = authResponse != null && StringUtils.hasLength(authResponse.getRespText()) ?
                        authResponse.getRespText() : "Error with the latest payment. Please try again";
                handlePaymentFailure(repository, tenantId, order, errorMessage);
            } else if (!transactionMap.containsKey(authResponse.getRetRef())) {
                Date responseDate = Utils.fromISO8601UTC(r.getResponseDate());
                Transaction transaction = buildCardTransaction(responseDate, authResponse);
                order.getTransactions().add(transaction);
                order.setCreditPaymentAmount(roundValue(order.getCreditPaymentAmount().add(transaction.getAmount())));
                order.setStatus(OrderStatus.PENDING.toString());
                break;
            }
        }
    }

    /**
     * @inheritDoc
     */
    public void addCashTransactionToOrder(Order order, BigDecimal amount, boolean isPaidOut) {
        Transaction savedCashTransaction = getTenderedCashTransaction(order);
        if (savedCashTransaction != null) {
            updateSavedCashTransaction(amount, savedCashTransaction);
        } else {
            createNewCashTransaction(amount, order, isPaidOut);
        }
        updateOrderCashPaymentAmount(order, amount);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void deleteTransactionFromOrder(Order order, String toDeleteTransactionId) {
        order.getTransactions().removeIf(transaction -> {
            String transactionId = transaction.getId();
            String parentTransactionId = transaction.getParentTransactionId();

            if (transactionId == null) {
                throw new OrderException("Cannot delete old order transaction");
            }

            boolean isToDelete = transactionId.equals(toDeleteTransactionId) ||
                    (parentTransactionId != null && parentTransactionId.equals(toDeleteTransactionId));

            if (isToDelete && transactionId.equals(toDeleteTransactionId)) {
                subtractDeletedTransactionAmount(transaction, order);
            }

            return isToDelete;
        });
    }

    private static void subtractDeletedTransactionAmount(Transaction transaction, Order order) {
        if (isCashTransaction(transaction)) {
            BigDecimal cashAmount = roundValue(order.getCashPaymentAmount().subtract(transaction.getAmount()));
            order.setCashPaymentAmount(cashAmount);
        }
    }

    private static Boolean isCashTransaction(Transaction transaction) {
        return transaction.getPaymentType().equals(PaymentType.CASH.toString());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void calculateChange(Order order) {
        Transaction savedCashTransaction = getTenderedCashTransaction(order);
        boolean balanceDueNegative = order.getBalanceDue().compareTo(BigDecimal.ZERO) < 0;
        boolean isChangeNeeded = balanceDueNegative && savedCashTransaction != null;

        if (isChangeNeeded) {
            Transaction savedChangeTransaction = getChangeTransaction(order);
            if (savedChangeTransaction != null) {
                savedChangeTransaction.setAmount(order.getBalanceDue().negate());
            } else {
                String parentCashTransactionId = savedCashTransaction.getId();
                createNewChangeTransaction(order, parentCashTransactionId);
            }
        }
    }

    /**
     * @inheritDoc
     */
    public boolean isFailedTransaction(OrderPaymentResponse r) {
        return Integer.parseInt(r.getStatus()) != HttpStatus.OK.value() ||
                (r.getResponse() != null && !r.getResponse().getRespStat().equals(APPROVED_CODE));
    }
}
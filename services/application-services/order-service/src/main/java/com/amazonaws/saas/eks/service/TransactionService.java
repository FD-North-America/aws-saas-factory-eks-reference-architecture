package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.order.model.Transaction;
import com.amazonaws.saas.eks.payment.dto.responses.OrderPaymentResponse;
import com.amazonaws.saas.eks.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TransactionService {

    /**
     * Adds a Cash Transaction to the given order
     * @param amount Cash amount
     * @param order {@link Order}
     * @param isPaidOut Is this for a Paid Out payment?
     */
    void addCashTransactionToOrder(Order order, BigDecimal amount, boolean isPaidOut);

    /**
     * Adds the successful Card payment to the Order as a transaction
     * @param repository {@link OrderRepository}
     * @param order {@link Order}
     * @param payments {@link OrderPaymentResponse}
     * @param transactionMap holds the current card payments keyed by their retref
     */
    void addCardPaymentToOrder(OrderRepository repository, String tenantId, Order order, List<OrderPaymentResponse> payments, Map<String, Transaction> transactionMap);


    /**
     * Deletes a specific transaction from the order object
     * @param order the order from which the transaction is to be deleted
     * @param toDeleteTransactionId the ID of the transaction to be deleted
     */
    void deleteTransactionFromOrder(Order order, String toDeleteTransactionId);

    /**
     * Once the order is fully paid, finalizing the state of the transactions
     * @param order {@link Order}
     */
    void calculateChange(Order order);

    /**
     * Checks if the given transaction is a failed transaction
     * @param r {@link OrderPaymentResponse}
     * @return true if the transaction is failed
     */
    boolean isFailedTransaction(OrderPaymentResponse r);
}
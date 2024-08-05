package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.product.dto.responses.saleshistory.ListSalesHistoryResponse;
import com.amazonaws.saas.eks.product.dto.responses.saleshistory.SalesHistoryResponse;
import com.amazonaws.saas.eks.product.model.SalesHistory;
import com.amazonaws.saas.eks.repository.SalesHistoryRepository;
import com.amazonaws.saas.eks.service.SalesHistoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.List;
import java.util.Map;

@Service
public class SalesHistoryServiceImpl implements SalesHistoryService {
    private static final Logger logger = LogManager.getLogger(SalesHistoryServiceImpl.class);

    private static final float DEFAULT_VALUE = 0;

    @Autowired
    private SalesHistoryRepository repository;

    @Override
    public ListSalesHistoryResponse getByProduct(String tenantId, String productId) {
        ListSalesHistoryResponse response = new ListSalesHistoryResponse();
        List<SalesHistory> salesHistories = repository.get(tenantId, productId);
        for (SalesHistory h : salesHistories) {
            Map<String, Float> amountMap = h.getMonthAmountMap();
            SalesHistoryResponse historyResponse = new SalesHistoryResponse();
            historyResponse.setYear(h.getYear());
            historyResponse.setJanAmount(amountMap.getOrDefault(Month.JANUARY.name(), DEFAULT_VALUE));
            historyResponse.setFebAmount(amountMap.getOrDefault(Month.FEBRUARY.name(), DEFAULT_VALUE));
            historyResponse.setMarchAmount(amountMap.getOrDefault(Month.MARCH.name(), DEFAULT_VALUE));
            historyResponse.setAprilAmount(amountMap.getOrDefault(Month.APRIL.name(), DEFAULT_VALUE));
            historyResponse.setMayAmount(amountMap.getOrDefault(Month.MAY.name(), DEFAULT_VALUE));
            historyResponse.setJuneAmount(amountMap.getOrDefault(Month.JUNE.name(), DEFAULT_VALUE));
            historyResponse.setJulyAmount(amountMap.getOrDefault(Month.JULY.name(), DEFAULT_VALUE));
            historyResponse.setAugAmount(amountMap.getOrDefault(Month.AUGUST.name(), DEFAULT_VALUE));
            historyResponse.setSeptAmount(amountMap.getOrDefault(Month.SEPTEMBER.name(), DEFAULT_VALUE));
            historyResponse.setOctAmount(amountMap.getOrDefault(Month.OCTOBER.name(), DEFAULT_VALUE));
            historyResponse.setNovAmount(amountMap.getOrDefault(Month.NOVEMBER.name(), DEFAULT_VALUE));
            historyResponse.setDecAmount(amountMap.getOrDefault(Month.DECEMBER.name(), DEFAULT_VALUE));
            response.getSaleHistories().add(historyResponse);
        }
        response.setCount(response.getSaleHistories().size());
        return response;
    }
}

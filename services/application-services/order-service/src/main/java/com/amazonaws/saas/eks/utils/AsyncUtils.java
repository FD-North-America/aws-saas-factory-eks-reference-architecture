package com.amazonaws.saas.eks.utils;

import com.amazonaws.saas.eks.clients.product.ProductServiceClient;
import com.amazonaws.saas.eks.exception.OrderException;
import com.amazonaws.saas.eks.order.model.LineItem;
import com.amazonaws.saas.eks.order.model.enums.LineItemType;
import com.amazonaws.saas.eks.product.dto.requests.product.UpdateCountRequestParams;
import com.amazonaws.saas.eks.product.dto.requests.product.UpdateProductCountRequest;
import feign.FeignException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AsyncUtils {
    private static final Logger logger = LogManager.getLogger(AsyncUtils.class);

    @Autowired
    private ProductServiceClient productServiceClient;

    @Async
    public void updateProductCounts(String tenantId, List<LineItem> lineItems) {
        List<LineItem> productLineItems = lineItems
                .stream()
                .filter(l -> !l.getType().equals(LineItemType.DISCOUNT.toString()))
                .collect(Collectors.toList());

        List<UpdateProductCountRequest> productCountRequests = new ArrayList<>();
        for (LineItem l : productLineItems) {
            UpdateProductCountRequest req = new UpdateProductCountRequest();
            req.setCount(l.getQuantity().floatValue());
            req.setId(l.getId());
            productCountRequests.add(req);
        }

        UpdateCountRequestParams params = new UpdateCountRequestParams();
        params.setProductCountRequests(productCountRequests);

        try {
            productServiceClient.updateProductCounts(tenantId, params);
        } catch (FeignException e) {
            String message = String.format("Error updating product counts. status: %d, message: %s", e.status(), e.getMessage());
            logger.error(message, e);
            throw new OrderException(message);
        }
    }
}

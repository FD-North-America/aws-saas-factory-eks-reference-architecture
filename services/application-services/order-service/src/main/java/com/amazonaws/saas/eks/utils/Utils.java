package com.amazonaws.saas.eks.utils;

import com.amazonaws.saas.eks.order.model.LineItem;
import com.amazonaws.saas.eks.order.model.UOMItem;
import com.amazonaws.saas.eks.product.dto.responses.product.ProductPricingResponse;
import com.amazonaws.saas.eks.product.dto.responses.uom.UOMResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Utils {
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
    private static final String UTC_TIMEZONE = "UTC";

    public static Date fromISO8601UTC(String dateStr) {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        df.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE));
        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BigDecimal roundValue(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public static void populateUOMList(ProductPricingResponse p, LineItem l) {
        for (UOMResponse u : p.getUomResponses()) {
            UOMItem uomItem = new UOMItem();
            uomItem.setId(u.getId());
            uomItem.setName(u.getName());
            l.getUomList().add(uomItem);
        }
    }

    /**
     * Takes in a list of {@link CompletableFuture} classes and returns the list when all the actions are completed
     *
     * @param futures list of {@link CompletableFuture}
     * @param <T>     any type
     */
    public static <T> void completeFutures(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allDone.thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.<T>toList()));
    }
}

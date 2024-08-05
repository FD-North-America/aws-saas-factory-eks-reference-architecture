package com.amazonaws.saas.eks.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Utils {

    public static final String dateFormatPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String utcTimeZone = "UTC";

    public static String toISO8601UTC(Date date) {
        DateFormat df = new SimpleDateFormat(dateFormatPattern);
        df.setTimeZone(TimeZone.getTimeZone(utcTimeZone));
        return df.format(date);
    }

    public static Date fromISO8601UTC(String dateStr) {
        DateFormat df = new SimpleDateFormat(dateFormatPattern);
        df.setTimeZone(TimeZone.getTimeZone(utcTimeZone));
        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Takes in a list of {@link CompletableFuture} classes and returns the list when all the actions are completed
     * @param futures list of {@link CompletableFuture}
     * @return list of completed futures
     * @param <T> any type
     */
    public static <T> CompletableFuture<List<T>> completeFutures(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return allDone.thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.<T>toList()));
    }
}

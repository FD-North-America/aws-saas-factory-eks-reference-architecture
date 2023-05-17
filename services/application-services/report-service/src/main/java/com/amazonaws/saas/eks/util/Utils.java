package com.amazonaws.saas.eks.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
    private static final String UTC_TIMEZONE = "UTC";

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static String toISO8601UTC(Date date) {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        df.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE));
        return df.format(date);
    }

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
}

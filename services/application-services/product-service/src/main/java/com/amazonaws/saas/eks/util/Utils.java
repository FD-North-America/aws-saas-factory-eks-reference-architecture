package com.amazonaws.saas.eks.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
}

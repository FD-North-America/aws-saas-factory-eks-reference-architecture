package com.amazonaws.saas.eks.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String SIMPLE_DATE_MONTH_PATTERN = "yyyy-MM";
    public static final String UTC_TIMEZONE = "UTC";

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    private static String toUTC(Date date) {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        df.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE));
        return df.format(date);
    }

    private static Date fromTimeZone(String dateStr, String timeZone) {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BigDecimal roundValue(BigDecimal value) {
        return value.setScale(2, RoundingMode.CEILING);
    }

    public static String convertToQueryDate(ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
        return date.format(formatter);
    }

    public static String convertToSimpleQueryDate(ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SIMPLE_DATE_MONTH_PATTERN);
        return date.format(formatter);
    }

    public static ZonedDateTime createToDate(String timeZone) {
        ZoneId preferredTimeZone = ZoneId.of(timeZone);
        ZonedDateTime now = ZonedDateTime.now(preferredTimeZone);
        return now.toLocalDate().plusDays(1).atStartOfDay(preferredTimeZone).withZoneSameInstant(ZoneId.of(UTC_TIMEZONE));
    }

    public static Date changeDateTimeZone(Date date, TimeZone timeZone) throws ParseException {
        if (date == null) {
            return null;
        }

        ZonedDateTime zonedDate = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of(timeZone.getID()));
        return Date.from(zonedDate.toInstant());
    }
}

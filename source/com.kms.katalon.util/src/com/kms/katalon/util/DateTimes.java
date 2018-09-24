package com.kms.katalon.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimes {
    public static final DateTimeFormatter ISO_8601_DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd");
    
    public static String format(Date date, DateTimeFormatter dateTimeFormatter) {
        OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()),
                ZoneId.systemDefault());
        return dateTimeFormatter.format(offsetDateTime);
    }

    public static String formatISO8601(Date date) {
        return format(date, ISO_8601_DATE_TIME_FORMATTER);
    }

    public static Date parseISO8601(String isoDate) {
        return new Date(Instant.from(ISO_8601_DATE_TIME_FORMATTER.parse(isoDate)).toEpochMilli());
    }
    
    public static Date toFirstDate(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}

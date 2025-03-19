package com.auctionmachine.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // String → LocalDateTime
    public static LocalDateTime toLocalDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    // LocalDateTime → String
    public static String toString(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }
}
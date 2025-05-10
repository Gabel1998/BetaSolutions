package com.example.betasolutions.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Check if a date is a workday (Monday to Friday)
    public static long countWorkdays(LocalDate start, LocalDate end) {
        return start.datesUntil(end.plusDays(1))
                .filter(d -> !d.getDayOfWeek().equals(DayOfWeek.SATURDAY) &&
                        !d.getDayOfWeek().equals(DayOfWeek.SUNDAY))
                .count();
    }

    // Format LocalDate to String
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(formatter) : null;
    }
}

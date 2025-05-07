package com.example.betasolutions.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateUtils {
    public static long countWorkdays(LocalDate start, LocalDate end) {
        return start.datesUntil(end.plusDays(1))
                .filter(d -> !d.getDayOfWeek().equals(DayOfWeek.SATURDAY) &&
                        !d.getDayOfWeek().equals(DayOfWeek.SUNDAY))
                .count();
    }
}

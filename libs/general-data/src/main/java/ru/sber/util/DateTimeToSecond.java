package ru.sber.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


public class DateTimeToSecond {

    public static long getDateTimeInSeconds(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneOffset.UTC).toInstant().getEpochSecond();
    }
}

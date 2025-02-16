package com.edara.edara.global.utils;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class Utilts {
    public static Duration calculatePeriod(LocalDateTime startDate, LocalDateTime endDate) {
        ZoneId zoneId = ZoneId.systemDefault(); // Use the system's default time zone or specify one explicitly
        Instant start = startDate.atZone(zoneId).toInstant();
        Instant end = endDate.atZone(zoneId).toInstant();

        return Duration.between(start, end);
    }
}

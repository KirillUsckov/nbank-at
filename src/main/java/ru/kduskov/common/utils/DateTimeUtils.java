package ru.kduskov.common.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeUtils {
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId MOSCOW = ZoneId.of("Europe/Moscow");

    private DateTimeUtils() {
    }

    public static long differenceWithCurrentMoscowTimeInSeconds(LocalDateTime utcTime) {
        ZonedDateTime serverTime = utcTime
                .atZone(UTC)
                .withZoneSameInstant(MOSCOW);

        ZonedDateTime currentTime = ZonedDateTime.now(MOSCOW);

        return Math.abs(Duration.between(serverTime, currentTime).getSeconds());
    }
}

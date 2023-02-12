package com.svetopolk.demo.service;

import com.svetopolk.demo.domain.Status;
import com.svetopolk.demo.validator.TimeZoneValidator;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

@Service
@Slf4j
public class SupportService {

    private final TimeZone timezone;
    private final LocalTime openLocal;
    private final LocalTime closeLocal;
    private final List<String> holidays;

    public SupportService(
            @Value("${support.time.timezone}") String timezone,
            @Value("${support.time.open}") String openLocal,
            @Value("${support.time.close}") String closeLocal,
            @Value("${support.holidays}") List<String> holidays
    ) {
        var openTime = validateTime(openLocal).split(":");
        var closeTime = validateTime(closeLocal).split(":");
        this.timezone = TimeZone.getTimeZone(validateTimeZone(timezone));
        this.openLocal = LocalTime.of(Integer.parseInt(openTime[0]), Integer.parseInt(openTime[1]));
        this.closeLocal = LocalTime.of(Integer.parseInt(closeTime[0]), Integer.parseInt(closeTime[1]));
        this.holidays = validateHolidays(holidays);
    }

    public Status getStatus() {
        return getStatus(ZonedDateTime.now());
    }

    public Status getStatus(ZonedDateTime time) {
        if (isWeekend(time) || isHoliday(time)) {
            return Status.DISABLED;
        }
        LocalTime supportLocalTime = time.withZoneSameInstant(timezone.toZoneId()).toLocalTime();

        if (openLocal.compareTo(supportLocalTime) < 0 && closeLocal.compareTo(supportLocalTime) > 0) {
            return Status.ENABLED;
        }
        return Status.DISABLED;
    }

    private boolean isHoliday(ZonedDateTime time) {
        return holidays.contains(formatDate(time));
    }

    public static boolean isWeekend(ZonedDateTime date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        return dayOfWeek == 6 || dayOfWeek == 7;
    }

    public static String formatDate(ZonedDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH));
    }

    public static String validateTimeZone(String timezone) {
        if (!TimeZoneValidator.isValid(timezone)) {
            throw new IllegalArgumentException("invalid or unknown time zone");
        }
        return timezone;
    }

    @NonNull
    private static String validateTime(String time) {
        if (!time.contains(":")) {
            throw new IllegalArgumentException("wrong time format: " + time);
        }
        return time;
    }

    private static List<String> validateHolidays(List<String> holidays) {
        var validMonths = Set.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        holidays.forEach(x -> {
                    if (x.length() < 5) {
                        throw new IllegalArgumentException("wrong holiday format: too short: " + x);
                    }
                    if (!validMonths.contains(x.substring(0, 3))) {
                        throw new IllegalArgumentException("wrong holiday format: unknown months: " + x);
                    }
                    if (x.charAt(3) != ' ') {
                        throw new IllegalArgumentException("wrong holiday format: no blank after month: " + x);
                    }
                    try {
                        Integer.parseInt(x.split(" ")[1]);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("wrong holiday format: expect number after month name: " + x);
                    }
                }
        );
        return holidays;
    }
}

package com.svetopolk.demo.service;

import com.svetopolk.demo.dto.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class SupportServiceTest {

    @Autowired
    SupportService supportService;

    @Test
    void checkOnLjubljanaTime() {
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2022, 12, 23, 8, 55, 0, 0, ZoneId.of("Europe/Ljubljana"))),
                is(Status.DISABLED)
        );
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2022, 12, 23, 9, 5, 0, 0, ZoneId.of("Europe/Ljubljana"))),
                is(Status.ENABLED)
        );
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2022, 12, 23, 14, 55, 0, 0, ZoneId.of("Europe/Ljubljana"))),
                is(Status.ENABLED)
        );
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2022, 12, 23, 15, 5, 0, 0, ZoneId.of("Europe/Ljubljana"))),
                is(Status.DISABLED)
        );
    }

    @Test
    void checkEastOfLjubljanaTime() {
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2022, 12, 23, 9, 55, 0, 0, ZoneId.of("Europe/Athens"))),
                is(Status.DISABLED)
        );
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2022, 12, 23, 10, 5, 0, 0, ZoneId.of("Europe/Athens"))),
                is(Status.ENABLED)
        );
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2022, 12, 23, 15, 55, 0, 0, ZoneId.of("Europe/Athens"))),
                is(Status.ENABLED)
        );
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2022, 12, 23, 16, 5, 0, 0, ZoneId.of("Europe/Athens"))),
                is(Status.DISABLED)
        );
    }

    @Test
    void checkWestOfLjubljanaTime() {
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2022, 12, 23, 7, 55, 0, 0, ZoneId.of("Europe/London"))),
                is(Status.DISABLED)
        );
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2022, 12, 23, 8, 5, 0, 0, ZoneId.of("Europe/London"))),
                is(Status.ENABLED)
        );
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2022, 12, 23, 13, 55, 0, 0, ZoneId.of("Europe/London"))),
                is(Status.ENABLED)
        );
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2022, 12, 23, 14, 5, 0, 0, ZoneId.of("Europe/London"))),
                is(Status.DISABLED)
        );
    }

    @Test
    void holiday() {
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2023, 2, 7, 12, 0, 0, 0, ZoneId.of("Europe/London"))),
                is(Status.ENABLED)
        );
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2023, 2, 8, 12, 0, 0, 0, ZoneId.of("Europe/London"))),
                is(Status.DISABLED)
        );
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2023, 2, 9, 12, 0, 0, 0, ZoneId.of("Europe/London"))),
                is(Status.ENABLED)
        );
    }

    @Test
    void weekend() {
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2023, 2, 3, 12, 0, 0, 0, ZoneId.of("Europe/London"))),
                is(Status.ENABLED)
        );
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2023, 2, 4, 12, 0, 0, 0, ZoneId.of("Europe/London"))),
                is(Status.DISABLED)
        );
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2023, 2, 5, 12, 0, 0, 0, ZoneId.of("Europe/London"))),
                is(Status.DISABLED)
        );
        assertThat(
                supportService.getStatus(ZonedDateTime.of(2023, 2, 6, 12, 0, 0, 0, ZoneId.of("Europe/London"))),
                is(Status.ENABLED)
        );
    }

    @Test
    void initWithWrongTimeZone() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new SupportService("Unknown", "9:20", "15:00", List.of())
        );
        assertThat(ex.getMessage(), is("invalid or unknown time zone"));
    }

    @Test
    void initWithWrongHoliday() {
        IllegalArgumentException ex1 = assertThrows(
                IllegalArgumentException.class,
                () -> new SupportService("GMT", "9:20", "15:00", List.of("J 1"))
        );
        assertThat(ex1.getMessage(), is("wrong holiday format: too short: J 1"));

        IllegalArgumentException ex2 = assertThrows(
                IllegalArgumentException.class,
                () -> new SupportService("GMT", "9:20", "15:00", List.of("Jan1"))
        );
        assertThat(ex2.getMessage(), is("wrong holiday format: too short: Jan1"));

        IllegalArgumentException ex3 = assertThrows(
                IllegalArgumentException.class,
                () -> new SupportService("GMT", "9:20", "15:00", List.of("Jam 1"))
        );
        assertThat(ex3.getMessage(), is("wrong holiday format: unknown months: Jam 1"));

        IllegalArgumentException ex4 = assertThrows(
                IllegalArgumentException.class,
                () -> new SupportService("GMT", "9:20", "15:00", List.of("Jan A"))
        );
        assertThat(ex4.getMessage(), is("wrong holiday format: expect number after month name: Jan A"));
    }

    @Test
    void initWithWrongTimeFormat() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new SupportService("GMT", "920", "15:00", List.of())
        );
        assertThat(ex.getMessage(), is("wrong time format: 920"));
    }

}
package com.mokakbob.common.config;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@TestConfiguration
@EnableJpaAuditing(dateTimeProviderRef = "testDateTimeProvider")
public class TestJpaConfig {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final Instant BASE_TIME = Instant.parse("2025-01-01T00:00:00Z");

    private static Duration offset = Duration.ZERO;

    public static void advanceBySeconds(long seconds) {
        offset = offset.plusSeconds(seconds);
    }

    @Bean
    public DateTimeProvider testDateTimeProvider() {
        return () -> {
            Clock dynamicClock = Clock.offset(Clock.fixed(BASE_TIME, ZONE_ID), offset);
            return Optional.of(LocalDateTime.now(dynamicClock));
        };
    }
}

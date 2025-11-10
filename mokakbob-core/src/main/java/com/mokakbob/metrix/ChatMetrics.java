package com.mokakbob.metrix;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class ChatMetrics implements MetricsRecorder {

    private static final String METRIC_ACTIVE_SESSIONS = "chat_active_sessions";
    private static final String METRIC_MESSAGES_SENT_TOTAL = "chat_send_messages_sent_total";
    private static final String METRIC_ERRORS_TOTAL = "chat_send_errors_total";
    private static final String METRIC_LATENCY_SECONDS = "chat_send_message_latency_seconds";
    private static final boolean ENABLE_HISTOGRAM = true;
    private static final double[] PERCENTILES = {0.95, 0.99};

    private final Counter messageSentCounter;
    private final Counter errorCounter;
    private final Timer latencyTimer;

    private final AtomicInteger activeSessions = new AtomicInteger(0);

    public ChatMetrics(MeterRegistry registry) {
        Gauge.builder(METRIC_ACTIVE_SESSIONS, activeSessions, AtomicInteger::get)
                .description("Number of active chat sessions")
                .tag("mode", "pubsub")
                .register(registry);

        this.messageSentCounter = Counter.builder(METRIC_MESSAGES_SENT_TOTAL)
                .description("Messages sent in chat event")
                .tag("mode", "pubsub")
                .register(registry);

        this.errorCounter = Counter.builder(METRIC_ERRORS_TOTAL)
                .description("Errors in chat event")
                .tag("mode", "pubsub")
                .register(registry);

        this.latencyTimer = Timer.builder(METRIC_LATENCY_SECONDS)
                .description("Chat message latency")
                .tag("mode", "pubsub")
                .publishPercentileHistogram(ENABLE_HISTOGRAM)
                .publishPercentiles(PERCENTILES)
                .register(registry);
    }

    @Override
    public void countRequest(String eventName) {
        messageSentCounter.increment();
    }

    @Override
    public void countError(String eventName) {
        errorCounter.increment();
    }

    @Override
    public void recordLatency(String eventName, long millis) {
        latencyTimer.record(millis, TimeUnit.MILLISECONDS);
    }

    public void incrementSession() {
        activeSessions.incrementAndGet();
    }

    public void decrementSession() {
        activeSessions.decrementAndGet();
    }
}

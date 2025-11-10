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

    // metric name
    private static final String TAG_MODE_KEY = "mode";
    private static final String TAG_MODE_VALUE = "pubsub";
    private static final String PREFIX_SEND = "chat_send_message";
    private static final String PREFIX_RECEIVE = "chat_receive_message";
    private static final String METRIC_ACTIVE_SESSIONS = "chat_active_sessions";
    private static final String SUFFIX_MESSAGES_TOTAL = "_sent_total";
    private static final String SUFFIX_ERRORS_TOTAL = "_errors_total";
    private static final String SUFFIX_LATENCY_SECONDS = "_latency_seconds";

    // metric description
    private static final String DESC_ACTIVE_SESSIONS = "Number of active chat sessions";
    private static final String DESC_SEND_TOTAL = "Total number of chat messages sent";
    private static final String DESC_SEND_ERRORS = "Number of errors occurred during sending chat messages";
    private static final String DESC_SEND_LATENCY = "Latency for sending chat messages";
    private static final String DESC_RECEIVE_ERRORS = "Number of errors occurred during receiving chat messages";
    private static final String DESC_RECEIVE_LATENCY = "Latency for receiving chat messages";

    // timer
    private static final boolean ENABLE_HISTOGRAM = true;
    private static final double[] PERCENTILES = {0.95, 0.99};

    // metric object
    private final AtomicInteger activeSessions = new AtomicInteger(0);

    private final Counter sendCounter;
    private final Counter sendErrorCounter;
    private final Timer sendLatencyTimer;

    private final Counter receiveErrorCounter;
    private final Timer receiveLatencyTimer;

    public ChatMetrics(MeterRegistry registry) {
        // Active sessions
        Gauge.builder(METRIC_ACTIVE_SESSIONS, activeSessions, AtomicInteger::get)
                .description(DESC_ACTIVE_SESSIONS)
                .tag(TAG_MODE_KEY, TAG_MODE_VALUE)
                .register(registry);

        // Send metrics
        this.sendCounter = buildCounter(registry,
                PREFIX_SEND + SUFFIX_MESSAGES_TOTAL, DESC_SEND_TOTAL);
        this.sendErrorCounter = buildCounter(registry,
                PREFIX_SEND + SUFFIX_ERRORS_TOTAL, DESC_SEND_ERRORS);
        this.sendLatencyTimer = buildTimer(registry,
                PREFIX_SEND + SUFFIX_LATENCY_SECONDS, DESC_SEND_LATENCY);

        // Receive metrics
        this.receiveErrorCounter = buildCounter(registry,
                PREFIX_RECEIVE + SUFFIX_ERRORS_TOTAL, DESC_RECEIVE_ERRORS);
        this.receiveLatencyTimer = buildTimer(registry,
                PREFIX_RECEIVE + SUFFIX_LATENCY_SECONDS, DESC_RECEIVE_LATENCY);
    }

    private Counter buildCounter(MeterRegistry registry, String name, String description) {
        return Counter.builder(name)
                .description(description)
                .tag(TAG_MODE_KEY, TAG_MODE_VALUE)
                .register(registry);
    }

    private Timer buildTimer(MeterRegistry registry, String name, String description) {
        return Timer.builder(name)
                .description(description)
                .tag(TAG_MODE_KEY, TAG_MODE_VALUE)
                .publishPercentileHistogram(ENABLE_HISTOGRAM)
                .publishPercentiles(PERCENTILES)
                .register(registry);
    }

    @Override
    public void countRequest(String eventName) {
        if (PREFIX_SEND.equals(eventName)) {
            sendCounter.increment();
        }
    }

    @Override
    public void countError(String eventName) {
        if (PREFIX_SEND.equals(eventName)) {
            sendErrorCounter.increment();
        } else if (PREFIX_RECEIVE.equals(eventName)) {
            receiveErrorCounter.increment();
        }
    }

    @Override
    public void recordLatency(String eventName, long millis) {
        if (PREFIX_SEND.equals(eventName)) {
            sendLatencyTimer.record(millis, TimeUnit.MILLISECONDS);
        } else if (PREFIX_RECEIVE.equals(eventName)) {
            receiveLatencyTimer.record(millis, TimeUnit.MILLISECONDS);
        }
    }

    public void incrementSession() {
        activeSessions.incrementAndGet();
    }

    public void decrementSession() {
        activeSessions.decrementAndGet();
    }
}

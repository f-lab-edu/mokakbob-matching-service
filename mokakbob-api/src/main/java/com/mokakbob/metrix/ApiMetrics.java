package com.mokakbob.metrix;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class ApiMetrics implements MetricsRecorder {

    private final MeterRegistry registry;

    public ApiMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void countRequest(String apiName) {
        Counter.builder(apiName + "_requests_total")
                .description("Total API requests for " + apiName)
                .register(registry)
                .increment();
    }

    @Override
    public void countError(String apiName) {
        Counter.builder(apiName + "_errors_total")
                .description("Total failed API requests for " + apiName)
                .register(registry)
                .increment();
    }

    @Override
    public void recordLatency(String apiName, long millis) {
        Timer.builder(apiName + "_response_time_seconds")
                .description("Response time for " + apiName)
                .register(registry)
                .record(millis, TimeUnit.MILLISECONDS);
    }
}

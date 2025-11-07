package com.mokakbob.metrix;

public interface MetricsRecorder {

    void countRequest(String name);

    void countError(String name);

    void recordLatency(String name, long millis);
}

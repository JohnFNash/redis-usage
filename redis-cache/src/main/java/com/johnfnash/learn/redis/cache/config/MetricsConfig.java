package com.johnfnash.learn.redis.cache.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

import io.micrometer.core.instrument.*;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter requestCounter(MeterRegistry meterRegistry) {
        return Counter.builder("test.request.total")
                .description("Total number of requests")
                .register(meterRegistry);
    }

    @Bean("gauge1")
    public AtomicInteger gauge1(MeterRegistry meterRegistry) {
        return meterRegistry.gauge("test.thread.pool.s1.size",
                Tags.of("type", "pool size"), new AtomicInteger(0));
    }

    @Bean("gauge2")
    public AtomicInteger gauge2(MeterRegistry meterRegistry) {
        return meterRegistry.gauge("test.thread.pool.s2.size",
                Tags.of("type", "pool size"), new AtomicInteger(0));
    }

//    private final AtomicInteger gauge1 = Metrics.gauge("test.thread.pool.s1.size", new AtomicInteger());

//    private final AtomicInteger gauge2 = Metrics.gauge("test.thread.pool.s2.size", new AtomicInteger());

    //private final Counter requestCounter = Metrics.counter("test.request.total", "task", "s1");

//    public AtomicInteger getGauge1() {
//        return gauge1;
//    }
//
//    public AtomicInteger getGauge2() {
//        return gauge2;
//    }

//    public Counter getRequestCounter() {
//        return requestCounter;
//    }

}

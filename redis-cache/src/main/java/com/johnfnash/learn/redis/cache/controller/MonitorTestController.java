package com.johnfnash.learn.redis.cache.controller;

import com.johnfnash.learn.redis.cache.config.MetricsConfig;
import io.micrometer.core.instrument.Counter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/monitor")
public class MonitorTestController {

    @Resource
    private Counter requestCounter;
    @Autowired
    @Qualifier("gauge1")
    private AtomicInteger counter1;
    @Autowired
    @Qualifier("gauge2")
    private AtomicInteger counter2;

    @RequestMapping("/request")
    public String requestMonitor() {
        counter1.set(10);
        counter2.set(11);
        requestCounter.increment();

        log.info("{}", counter1);
        log.info("{}", counter2);
        log.info("{}", requestCounter);
        return "ok";
    }

}

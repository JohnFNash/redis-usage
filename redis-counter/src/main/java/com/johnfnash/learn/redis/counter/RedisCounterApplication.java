package com.johnfnash.learn.redis.counter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@SpringBootApplication
@EnableScheduling
public class RedisCounterApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedisCounterApplication.class, args);
    }

}
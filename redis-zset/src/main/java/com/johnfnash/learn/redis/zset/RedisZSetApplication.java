package com.johnfnash.learn.redis.zset;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RedisZSetApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisZSetApplication.class, args);
    }

}

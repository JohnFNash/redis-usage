package com.johnfnash.learn.redis.session;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800, redisNamespace = "r:s")
public class RedisSessionApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisSessionApplication.class, args);
    }

}

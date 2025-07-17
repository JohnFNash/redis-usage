package com.johnfnash.learn.redis.list.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisConsumer {

    @Autowired
    @Qualifier("redisRawTemplate")
    private RedisTemplate<String, String> redisTemplate;

    // 消费消息
    public void consume(String key) {
        while (true) {
            try {
                // 使用BLPOP阻塞弹出消息，超时时间为0表示无限等待
                String message = redisTemplate.opsForList().leftPop(key, 0, TimeUnit.SECONDS);
                if (message != null) {
                    log.info("Received message: " + message);
                }
            } catch (Exception e) {
                log.error("消息消费报异常：{}", e);
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e1) {
                }
            }
        }
    }

}
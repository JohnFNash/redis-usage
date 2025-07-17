package com.johnfnash.learn.redis.list.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisProducer {

    @Autowired
    @Qualifier("redisRawTemplate")
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 发送消息
     * @param key
     * @param message
     */
    public void sendMessage(String key, String message) {
        redisTemplate.opsForList().rightPush(key, message);
    }

}

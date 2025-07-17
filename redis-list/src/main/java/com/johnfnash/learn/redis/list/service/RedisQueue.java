package com.johnfnash.learn.redis.list.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisQueue {

    @Autowired
    @Qualifier("redisRawTemplate")
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 入队
     * @param key
     * @param value
     */
    public void enqueue(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 出队
     * @param key
     * @return
     */
    public String dequeue(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 获取队列长度
     * @param key
     * @return
     */
    public long size(String key) {
        return redisTemplate.opsForList().size(key);
    }

}

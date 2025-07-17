package com.johnfnash.learn.redis.list.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisStackService {

    @Autowired
    @Qualifier("redisRawTemplate")
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 压栈
     * @param key
     * @param value
     */
    public void push(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 出栈
     * @param key
     * @return
     */
    public String pop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 栈顶元素。只是查看，不出栈
     * @param key
     * @return
     */
    public String peek(String key) {
        return redisTemplate.opsForList().index(key, 0);
    }

    /**
     * 栈大小
     * @param key
     * @return
     */
    public long size(String key) {
        return redisTemplate.opsForList().size(key);
    }

}

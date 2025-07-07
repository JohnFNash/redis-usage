package com.johnfnash.learn.redis.counter.redis.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class StringRedisStringService {

    @Resource(name = "redisRawTemplate")
	private RedisTemplate<String, String> redisTemplate;

	public DataType type(String redisKey){
        return redisTemplate.type(redisKey);
    }

    public Boolean delete(String redisKey){
        return redisTemplate.delete(redisKey);
    }

    public String get(String redisKey){
        return getOpsForValue().get(redisKey);
    }

    private ValueOperations<String, String> getOpsForValue() {
        return redisTemplate.opsForValue();
    }

    public void set(String redisKey, String value) {
    	getOpsForValue().set(redisKey, value);
    }

    public Boolean setIfAbsent(String key, String value) {
    	return getOpsForValue().setIfAbsent(key, value);
    }

    public Boolean setIfAbsentWithExpire(String key, String value, Duration duration) {
        return getOpsForValue().setIfAbsent(key, value, duration);
    }

    public void set(String redisKey, String value, long aliveTime, TimeUnit timeUnit) {
        getOpsForValue().set(redisKey, value, aliveTime, timeUnit);
    }

    public void expire(String key, Duration duration) {
        redisTemplate.expire(key, duration);
    }

    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    public Long increment(String key) {
        return getOpsForValue().increment(key);
    }

}

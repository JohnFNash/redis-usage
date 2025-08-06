package com.johnfnash.learn.redis.zset.service;

import com.johnfnash.learn.redis.zset.redis.StringRedisZSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Function;

@Service
public class DelayQueueService {

    private static final String DELAY_QUEUE_KEY = "delay_queue";

    @Autowired
    private StringRedisZSetService stringRedisZSetService;

    public void addToDelayQueue(String message, long delayTime) {
        stringRedisZSetService.add(DELAY_QUEUE_KEY, message, System.currentTimeMillis() + delayTime);
    }

    public void pollAndProcessDelayedMessages(Function<String, Void> callback) {
        Set<String> messages = stringRedisZSetService.rangeWithScores(DELAY_QUEUE_KEY, 0, System.currentTimeMillis());
        for (String message : messages) {
            // 处理消息
            callback.apply(message);
            // 从延迟队列中删除已处理的消息
            stringRedisZSetService.remove(DELAY_QUEUE_KEY, message);
        }
    }

}

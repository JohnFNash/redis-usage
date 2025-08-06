package com.johnfnash.learn.redis.zset.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DelayQueueSchedule {

    @Autowired
    private DelayQueueService delayQueueService;

    // 每隔一段时间进行轮询并处理延迟消息
    @Scheduled(fixedDelay = 5000)
    public void pollAndProcessDelayedMessages() {
        delayQueueService.pollAndProcessDelayedMessages(message -> {
            // 根据业务需求进行消息处理
            log.info("Processing message: " + message + " " + System.currentTimeMillis());
            return null;
        });
    }

}

package com.johnfnash.learn.redis.zset.controller;

import com.johnfnash.learn.redis.zset.service.DelayQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DelayQueueController {

     @Autowired
     private DelayQueueService delayQueueService;

     @GetMapping(value = "/addDelayMsg")
     public void addToDelayQueue(String message, long delayTime) {
         delayQueueService.addToDelayQueue(message, delayTime);
         System.out.println("添加延迟消息成功：" + System.currentTimeMillis());
     }

}

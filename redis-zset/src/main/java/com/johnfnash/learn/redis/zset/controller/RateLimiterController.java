package com.johnfnash.learn.redis.zset.controller;

import com.johnfnash.learn.redis.zset.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RateLimiterController {

    @Autowired
    private RateLimiterService rateLimiterService;

    @GetMapping("/request")
    public String request(String userId) {
        boolean passed = rateLimiterService.request(userId);
        if (passed) {
            return "访问成功";
        }
        return "访问失败，访问太过频繁";
    }

}

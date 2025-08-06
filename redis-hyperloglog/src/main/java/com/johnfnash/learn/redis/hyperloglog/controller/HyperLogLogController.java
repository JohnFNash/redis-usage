package com.johnfnash.learn.redis.hyperloglog.controller;

import com.johnfnash.learn.redis.hyperloglog.redis.StringRedisHyperLogLogService;
import com.johnfnash.learn.redis.hyperloglog.service.BigDataDeduplicationService;
import com.johnfnash.learn.redis.hyperloglog.service.HyperLogLogAccuracyTest;
import com.johnfnash.learn.redis.hyperloglog.service.HyperLogLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HyperLogLogController {

    @Autowired
    private HyperLogLogService hyperLogLogService;
    @Autowired
    private StringRedisHyperLogLogService redisHyperLogLogService;
    @Autowired
    private HyperLogLogAccuracyTest hyperLogLogAccuracyTest;
    @Autowired
    private BigDataDeduplicationService bigDataDeduplicationService;

    // 获取IP去重后的网页访问量
    @RequestMapping(value = "/hyperloglog/uv", method = RequestMethod.GET)
    public long uv() {
        return redisHyperLogLogService.pfCount(HyperLogLogService.KEY);
    }

    @RequestMapping(value = "/hyperloglog/init", method = RequestMethod.GET)
    public String init() {
        hyperLogLogService.init();
        return "success";
    }

    @RequestMapping(value = "/hyperloglog/testAccuracy", method = RequestMethod.GET)
    public String testAccuracy(String userId) {
        hyperLogLogAccuracyTest.testAccuracy();
        return "success";
    }

    @RequestMapping(value = "/hyperloglog/bigDataDeduplication", method = RequestMethod.GET)
    public String bigDataDeduplication(String logFilePath) {
        bigDataDeduplicationService.processLogFile(logFilePath);
        return "success";
    }

    @RequestMapping(value = "/hyperloglog/getUniqueIPs", method = RequestMethod.GET)
    public long getUniqueIPs() {
        return bigDataDeduplicationService.getUniqueIPs();
    }

}

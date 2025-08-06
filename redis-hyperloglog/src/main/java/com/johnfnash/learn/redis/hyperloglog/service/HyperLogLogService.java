package com.johnfnash.learn.redis.hyperloglog.service;

import com.johnfnash.learn.redis.hyperloglog.redis.StringRedisHyperLogLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class HyperLogLogService {

    @Autowired
    private StringRedisHyperLogLogService redisHyperLogLogService;

    public static final String KEY = "hll";

    public void init() {
        log.info("------模拟后台有用户点击首页，每个用户来自不同ip地址");
        //  测试模拟，实际不用这种写法
        new Thread(() -> {
            Set<String> ips = new HashSet<>(256);
            String ip;
            for (int i = 1; i <= 256; i++) {
                Random r = new Random();
                ip = "192.168.1." + r.nextInt(256);
                ips.add(ip);
                redisHyperLogLogService.pfAdd(KEY, ip);
                //暂停200ms线程
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            long size = redisHyperLogLogService.pfCount(KEY);
            long actualSize = ips.size();
            log.info("------模拟后台有用户点击首页，每个用户来自不同ip地址，模拟结束，估算数量：{}，实际数量：{}", size, actualSize);
        }).start();
    }

}

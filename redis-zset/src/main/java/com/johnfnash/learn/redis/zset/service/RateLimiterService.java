package com.johnfnash.learn.redis.zset.service;

import com.johnfnash.learn.redis.zset.redis.StringRedisZSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RateLimiterService {

    private static final String RATE_LIMITER_KEY = "rate_limiter:";
    // 时间段内最大访问次数
    private static final int LIMIT = 5;
    // 时间窗口 1分钟
    private static final long TIME_WINDOW = 60 * 1000;

    @Autowired
    private StringRedisZSetService stringRedisZSetService;

    public boolean request(String userId) {
        Long now = Instant.now().toEpochMilli();

        // 清理过期数据
        cleanup(userId);

        String redisKey = RATE_LIMITER_KEY + userId;
        Long count = stringRedisZSetService.countByScoreRange(redisKey, now - TIME_WINDOW, now);
        if (count < LIMIT) {
            // 未达到上限，允许请求
            stringRedisZSetService.add(redisKey, now.toString(), now);
            return true;
        }

        // 达到上限，不允许请求
        return false;
    }

    /**
     * 清理过期数据
     * @param userId
     */
    public void cleanup(String userId) {
        stringRedisZSetService.removeRangeByScore(RATE_LIMITER_KEY + userId, 0, Instant.now().toEpochMilli() - TIME_WINDOW);
    }

}

package com.johnfnash.learn.redis.cache.service;

import com.johnfnash.learn.redis.cache.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 热门缓存预热
 */
@Component
public class CacheWarmer implements CommandLineRunner {

    private final UserService userService;

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheWarmer(UserService userService, RedisTemplate<String, Object> redisTemplate) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 系统启动时执行缓存预热
     */
    @Override
    public void run(String... args) {
        System.out.println("Performing cache warming...");

        // 加载热门记录到缓存
        List<User> userList = userService.findTop10();
        for (User user : userList) {
            String cacheKey = "user:" + user.getId();
            redisTemplate.opsForValue().set(cacheKey,  user);

            // 设置差异化缓存过期时间，避免同时过期
            long randomTtl = 3600 + (long)(Math.random() * 1800); // 1小时到1.5小时之间的随机值
            redisTemplate.expire(cacheKey, randomTtl, java.util.concurrent.TimeUnit.SECONDS);
        }

        System.out.println("Cache warming completed, loaded " + userList.size() + " users");
    }

    /**
     * 定时更新热点数据缓存，每小时执行一次
     */
    @Scheduled(fixedRate = 3600000)
    public void refreshHotDataCache() {
        System.out.println("Refreshing hot data cache...");

        // 获取最新的热点数据
        List<User> userList = userService.findTop10();

        // 更新缓存
        for (User user : userList) {
            redisTemplate.opsForValue().set("user:" + user.getId(),  user);
        }

        System.out.println("Refreshing cache completed, loaded " + userList.size() + " users");
    }

}

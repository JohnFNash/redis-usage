package com.johnfash.learn.redis.bitmap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;

@Service
public class DDOSProtection {

    private String currentDateKey;

    @Autowired
    private IPAddressTracker ipTracker;

    @Resource(name = "redisRawTemplate")
    private RedisTemplate<String, String> redisTemplate;

    public DDOSProtection() {
        updateDateKey();
    }

    // 更新日期Key
    private void updateDateKey() {
        String date = java.time.LocalDate.now().toString();
        this.currentDateKey = "ip:access:count:" + date;
    }

    /**
     * 记录IP访问并检查是否超过阈值
     * @return true表示IP应被阻止
     */
    public boolean shouldBlockIP(String ipAddress, int accessLimit) {
        // 先检查是否已在黑名单
        if (ipTracker.isInBlackList(ipAddress)) {
            return true;
        }

        // 记录访问
        long ipValue = ipTracker.ipToLong(ipAddress);
        // 记录访问次数并检查
        String accessKey = currentDateKey + ":" + ipValue;
        long accessCount = redisTemplate.opsForValue().increment(accessKey);

        // 设置24小时过期
        if (accessCount == 1) {
            redisTemplate.expire(accessKey, Duration.ofHours(24));
        }

        // 检查是否超过访问限制
        if (accessCount > accessLimit) {
            // 添加到黑名单
            ipTracker.addToBlackList(ipAddress);
            return true;
        }

        return false;

    }

}

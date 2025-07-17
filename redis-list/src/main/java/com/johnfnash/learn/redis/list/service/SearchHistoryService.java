package com.johnfnash.learn.redis.list.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchHistoryService {

    private static final String REDIS_KEY_PREFIX = "history:";
    private static final int MAX_HISTORY_ITEMS = 10; // 保留最近10条记录

    @Autowired
    @Qualifier("redisRawTemplate")
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 添加历史记录
     * @param userId
     * @param keyword
     */
    public void addHistory(String userId, String keyword) {
        String key = getHistoryKey(userId);
        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            connection.openPipeline();
            // 先删除已存在的记录
            connection.lRem(key.getBytes(), 0, keyword.getBytes());
            // 添加新的记录
            connection.lPush(key.getBytes(), keyword.getBytes());
            // 截取列表，只保留最近10条记录
            connection.lTrim(key.getBytes(), 0, MAX_HISTORY_ITEMS - 1);
            return null;
        });
    }

    /**
     * 获取历史记录
     * @param userId
     * @param count
     * @return
     */
    public List<String> getHistory(String userId, int count) {
        return redisTemplate.opsForList().range(getHistoryKey(userId), 0, count - 1);
    }

    private static String getHistoryKey(String userId) {
        return REDIS_KEY_PREFIX + userId;
    }

    /**
     * 删除历史记录
     * @param userId
     */
    public void deleteHistory(String userId) {
        redisTemplate.delete(getHistoryKey(userId));
    }

}

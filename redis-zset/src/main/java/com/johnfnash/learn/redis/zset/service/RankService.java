package com.johnfnash.learn.redis.zset.service;

import com.johnfnash.learn.redis.zset.redis.StringRedisZSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 排行榜服务。分数必须为整数
 */
@Service
public class RankService {

    @Autowired
    private StringRedisZSetService redisZSetService;

    private static final String RANK_KEY = "rank:";
    // 时间戳小数位数
    private static final int TIMESTAMP_DIGITS = 13;

    /**
     * 添加用户到排行榜
     * @param userId
     * @param score
     * @param reversed 分数是否倒序排，默认正序。正序时越早达到越排在前面；倒序时越早达到越排在后面
     */
    public void addUserToLeaderboard(String userId, int score, boolean reversed) {
        // 获取当前时间戳，这里为毫秒级精度
        long timestamp = reversed ? Long.MAX_VALUE - System.currentTimeMillis() : System.currentTimeMillis();
        // 将时间戳转换为小数部分并加到原始分数上
        double combinedScore = score + (timestamp / Math.pow(10, TIMESTAMP_DIGITS)); // timestampDigits 是时间戳的小数位数
        // 添加到 Redis Zset 中
        redisZSetService.add(RANK_KEY, userId, combinedScore);
    }

    /**
     * 获取排行榜前 n 个用户
     * @param n
     * @param reversed 分数是否倒序排，默认正序。正序时越早达到越排在前面；倒序时越早达到越排在后面
     * @return
     */
    public Set<String> getTopUsers(int n, boolean reversed) {
        if (reversed) {
            return redisZSetService.reverseRange(RANK_KEY, 0, n - 1);
        } else {
            return redisZSetService.range(RANK_KEY, 0, n - 1);
        }
    }

    /**
     * 获取某个用户的排名
     * @param userId
     * @param reversed
     * @return
     */
    public Long getUserRank(String userId, boolean reversed) {
        if (reversed) {
            return redisZSetService.reverseRank(RANK_KEY, userId) + 1;
        } else {
            return redisZSetService.rank(RANK_KEY, userId) + 1;
        }
    }

    /**
     * 获取某个用户的得分
     *
     * @param userId
     * @return
     */
    public int getUserScore(String userId) {
        Double score = redisZSetService.getScore(RANK_KEY, userId);
        return score == null ? 0 : score.intValue();
    }

    /**
     * 获取用户的排名和得分
     * @param userId
     * @return
     */
    public Map<String, Object> getUserRankAndScore(String userId, boolean reversed) {
        Map<String, Object> result = new HashMap<>();
        result.put("rank", getUserRank(userId, reversed));
        result.put("score", getUserScore(userId));
        return result;
    }

}

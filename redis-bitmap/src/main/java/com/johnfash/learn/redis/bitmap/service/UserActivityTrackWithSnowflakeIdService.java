package com.johnfash.learn.redis.bitmap.service;

import com.johnfash.learn.redis.bitmap.redis.StringRedisBitmapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 在线用户统计（用户id为雪花id）
 */
@Service
public class UserActivityTrackWithSnowflakeIdService {

    private static final String USER_ACTIVITY_TRACK_KEY = "user_activity_track:snowflake:";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final int MAX_ITEM_IN_ONE_BITMAP = 100000000;

    @Autowired
    private StringRedisBitmapService stringRedisBitmapService;
    private RedisTemplate<Object, Object> redisTemplate;
//    @Autowired
//    private static SnowflakeIdGenerator snowflakeIdGenerator;

    /**
     * 记录用户活跃
     * @param userId
     * @param date
     */
    public void trackUserActivity(long userId, LocalDate date) {
        stringRedisBitmapService.setBit(getKey(date, userId), getOffset(userId), true);
    }

    /**
     * 获取日活跃用户数(DAU)
     */
    public long getDailyActiveUserCount(LocalDate date) {
        String keyPrefix = getScanKeyPrefix(date);
        return stringRedisBitmapService.bitCountWithShard(keyPrefix);
    }

    private static String getScanKeyPrefix(LocalDate date) {
        return USER_ACTIVITY_TRACK_KEY + "*:" + date.format(DATE_FORMATTER);
    }

    /**
     * 获取月活跃用户数(MAU)
     * @param year
     * @param month
     * @return
     */
    public long getMonthlyActiveUsers(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 扫描整月的所有日期的活跃用户所在key
        List<String> allKeyList = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<String> keyList = stringRedisBitmapService.scan(getScanKeyPrefix(date));
            allKeyList.addAll(keyList);
        }
        // 将key按分片进行分组
        Map<String, List<String>> shardKeyMap = allKeyList.stream().collect(Collectors.groupingBy(k -> {
            int secondPosition = k.indexOf(":", k.indexOf(":") + 1);
            return k.substring(0, secondPosition + 1);
        }));

       // 对于每个分片，对每一天进行异或计算，算出每个分片的结果
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String shardKeyPregix : shardKeyMap.keySet()) {
                List<String> keyList = shardKeyMap.get(shardKeyPregix);
                byte[][] byteArrs = new byte[keyList.size()][];
                keyList.stream().map(k -> k.getBytes()).collect(Collectors.toList()).toArray(byteArrs);
                connection.bitOp(RedisStringCommands.BitOperation.OR, shardKeyPregix.getBytes(), byteArrs);
            }
            return null;
        });

        // 计算总活跃用户数
        List<String> shardKeyList = new ArrayList<>(shardKeyMap.keySet());
        long mau = stringRedisBitmapService.sumBitCount(shardKeyList);

        // 清理临时键
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String shardKeyPregix : shardKeyMap.keySet()) {
                stringRedisBitmapService.del(shardKeyPregix);
            }
            return null;
        });

        return mau;
    }

    /**
     * 获取key前缀
     * @param date
     * @return
     */
    private static String getKey(LocalDate date, long userId) {
        long shard = userId / MAX_ITEM_IN_ONE_BITMAP;
        return  USER_ACTIVITY_TRACK_KEY + shard + ":" + date.format(DATE_FORMATTER);
    }

    private static long getOffset(long userId) {
        return userId % MAX_ITEM_IN_ONE_BITMAP;
    }

    /**
     * 判断两天的活跃用户重合度 (留存率相关)
     */
    public long getActiveUserOverlap(LocalDate date1, LocalDate date2) {
        // 扫描给定的两个日期的活跃用户所在key
        List<String> allKeyList = new ArrayList<>();
        List<String> key1List = stringRedisBitmapService.scan(getScanKeyPrefix(date1));
        List<String> key2List = stringRedisBitmapService.scan(getScanKeyPrefix(date2));
        allKeyList.addAll(key1List);
        allKeyList.addAll(key2List);

        // 将key按分片进行分组
        Map<String, List<String>> shardKeyMap = allKeyList.stream().collect(Collectors.groupingBy(k -> {
            int secondPosition = k.indexOf(":", k.indexOf(":") + 1);
            return k.substring(0, secondPosition + 1);
        }));

        // 对于每个分片，对每一天进行and计算，算出每个分片的结果
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String shardKeyPregix : shardKeyMap.keySet()) {
                List<String> keyList = shardKeyMap.get(shardKeyPregix);
                byte[][] byteArrs = new byte[keyList.size()][];
                keyList.stream().map(k -> k.getBytes()).collect(Collectors.toList()).toArray(byteArrs);
                connection.bitOp(RedisStringCommands.BitOperation.AND, shardKeyPregix.getBytes(), byteArrs);
            }
            return null;
        });

        // 计算总的两天的活跃用户重合度
        List<String> shardKeyList = new ArrayList<>(shardKeyMap.keySet());
        long overlap = stringRedisBitmapService.sumBitCount(shardKeyList);

        // 清理临时键
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String shardKeyPregix : shardKeyMap.keySet()) {
                stringRedisBitmapService.del(shardKeyPregix);
            }
            return null;
        });

        return overlap;
    }

    /**
     * 次日留存率计算
     * @param date
     * @return
     */
    public double getRetentionRate(LocalDate date) {
        LocalDate nextDate = date.plusDays(1);

        // 当天活跃用户数
        long todayActive = getDailyActiveUserCount(date);
        if (todayActive == 0) return 0.0;

        // 计算当天活跃用户中第二天仍活跃的用户数
        long overlap = getActiveUserOverlap(date, nextDate);

        // 计算留存率
        return (double) overlap / todayActive;
    }

}

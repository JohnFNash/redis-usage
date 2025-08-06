package com.johnfash.learn.redis.bitmap.service;

import com.johnfash.learn.redis.bitmap.redis.StringRedisBitmapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 在线用户统计
 */
@Service
public class UserActivityTrackService {

    private static final String USER_ACTIVITY_TRACK_KEY = "user_activity_track:";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private StringRedisBitmapService stringRedisBitmapService;

    /**
     * 记录用户活跃
     * @param userId
     * @param date
     */
    public void trackUserActivity(long userId, LocalDate date) {
        stringRedisBitmapService.setBit(getKey(date), userId, true);
    }

    /**
     * 获取日活跃用户数(DAU)
     */
    public long getDailyActiveUserCount(LocalDate date) {
        return stringRedisBitmapService.bitCount(getKey(date));
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

        // 创建临时结果键
        String destKey = "temp:mau:" + year + month;
        // 收集整月的所有日期的活跃用户
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dayKey = getKey(date);
            // 使用OR操作合并日活跃数据
            stringRedisBitmapService.bitOpOr(destKey, destKey, dayKey);
        }

        // 计算总活跃用户数
        long mau = stringRedisBitmapService.bitCount(destKey);

        // 清理临时键
        stringRedisBitmapService.del(destKey);

        return mau;
    }

    /**
     * 获取key前缀
     * @param date
     * @return
     */
    private static String getKey(LocalDate date) {
        return USER_ACTIVITY_TRACK_KEY + date.format(DATE_FORMATTER);
    }

    /**
     * 判断两天的活跃用户重合度 (留存率相关)
     */
    public long getActiveUserOverlap(LocalDate date1, LocalDate date2) {
        String key1 = getKey(date1);
        String key2 = getKey(date2);

        // 使用AND操作找出两天都活跃的用户
        String destKey = "temp:overlap:" + date1.format(DATE_FORMATTER) + ":" + date2.format(DATE_FORMATTER);
        stringRedisBitmapService.bitOpAnd(destKey, key1, key2);
        long overlap = stringRedisBitmapService.bitCount(destKey);

        stringRedisBitmapService.del(destKey);

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

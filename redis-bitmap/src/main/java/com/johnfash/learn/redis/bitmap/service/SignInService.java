package com.johnfash.learn.redis.bitmap.service;

import com.johnfash.learn.redis.bitmap.redis.StringRedisBitmapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 按月签到服务
 */
@Service
public class SignInService {

    private static final String PREFIX_KEY = "signIn:";
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    @Autowired
    private StringRedisBitmapService redisBitmapService;

    /**
     * 签到
     * @param userId
     * @param date
     * @return
     */
    public Boolean signIn(long userId, LocalDate date) {
        String signKey = getSignKey(userId, date);
        int offset = getOffset(date);
        return redisBitmapService.setBit(signKey, offset, true);
    }

    private static int getOffset(LocalDate date) {
        // Redis BitMap是0-based
        return date.getDayOfMonth() - 1;
    }

    /**
     * 检查用户是否签到
     * @param userId
     * @param date
     * @return
     */
    public Boolean hasSignedIn(long userId, LocalDate date) {
        String signKey = getSignKey(userId, date);
        int offset = getOffset(date);
        return redisBitmapService.getBit(signKey, offset);
    }

    /**
     * 获取用户当月签到次数
     * @param userId
     * @param date
     * @return
     */
    public long getMonthlySignCount(long userId, LocalDate date) {
        String signKey = getSignKey(userId, date);
        return redisBitmapService.bitCount(signKey);
    }

    /**
     * 获取用户当月首次签到日期
     */
    public long getFirstSignInDay(long userId, LocalDate date) {
        String signKey = getSignKey(userId, date);
        long position = redisBitmapService.bitPos(signKey, true);
        return position == -1 ? 0 : position + 1; // 转换回自然日
    }

    /**
     * 获取用户当月连续签到天数
     */
    public long getMonthlySignInDays(long userId, LocalDate date) {
        String signKey = getSignKey(userId, date);
        int dayOfMonth = date.getDayOfMonth();
        int endOffset = dayOfMonth - 1;

        // 从当天往前查找连续签到的天数
        int count = 0;
        for (int i = endOffset; i>=0; i--) {
            if (!redisBitmapService.getBit(signKey, i)) {
                break;
            }
            count++;
        }
        return count;
    }

    /**
     * 构建本月签到key
     */
    private String getSignKey(long userId, LocalDate date) {
        return PREFIX_KEY + userId + ":" + date.format(MONTH_FORMATTER);
    }

}

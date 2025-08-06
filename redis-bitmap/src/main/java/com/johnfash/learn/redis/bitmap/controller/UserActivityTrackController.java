package com.johnfash.learn.redis.bitmap.controller;

import com.johnfash.learn.redis.bitmap.service.UserActivityTrackWithSnowflakeIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/useractivity")
public class UserActivityTrackController {

//    @Autowired
//    private UserActivityTrackService userActivityTrackService;
    @Autowired
    private UserActivityTrackWithSnowflakeIdService userActivityTrackService;

    @RequestMapping("/track")
    public String trackUserActivity(long userId, String date) {
        userActivityTrackService.trackUserActivity(userId, LocalDate.parse(date));
        return "success";
    }

    @RequestMapping("/daily")
    public long getDailyActiveUserCount(String date) {
        return userActivityTrackService.getDailyActiveUserCount(LocalDate.parse(date));
    }

    @RequestMapping("/monthly")
    public long getMonthlyActiveUsers(int year, int month) {
        return userActivityTrackService.getMonthlyActiveUsers(year, month);
    }

    @RequestMapping("/overlap")
    public long getActiveUserOverlap(String date1, String date2) {
        return userActivityTrackService.getActiveUserOverlap(LocalDate.parse(date1), LocalDate.parse(date2));
    }

    @RequestMapping("/retention")
    public double getRetentionRate(String date) {
        return userActivityTrackService.getRetentionRate(LocalDate.parse(date));
    }

}

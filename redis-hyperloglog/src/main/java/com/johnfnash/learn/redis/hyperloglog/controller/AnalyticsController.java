package com.johnfnash.learn.redis.hyperloglog.controller;

import com.johnfnash.learn.redis.hyperloglog.redis.StringRedisHyperLogLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

@RestController
public class AnalyticsController {

    @Autowired
    private StringRedisHyperLogLogService redisHyperLogLogService;

    /**
     * 记录用户访问
     * @param userId
     * @return
     */
    @GetMapping("/track")
    public ResponseEntity<String> trackVisit(@RequestParam String userId) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        redisHyperLogLogService.trackUniqueVisitor(today, userId);
        return ResponseEntity.ok("success");
    }

    /**
     * 获取今日UV
     * @return
     */
    @GetMapping("/uv/today")
    public ResponseEntity<Long> getTodayUV() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return ResponseEntity.ok(redisHyperLogLogService.getUniqueVisitors(today));
    }

    /**
     * 获取最近7天UV
     * @return
     */
    @GetMapping("/uv/week")
    public ResponseEntity<Long> getWeekUV() {
        String[] dates = IntStream.range(0, 7).mapToObj(i -> LocalDate.now().minusDays(i))
                .map(date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .toArray(String[]::new);
        return ResponseEntity.ok(redisHyperLogLogService.getUniqueVisitorsRange(dates));
    }

}

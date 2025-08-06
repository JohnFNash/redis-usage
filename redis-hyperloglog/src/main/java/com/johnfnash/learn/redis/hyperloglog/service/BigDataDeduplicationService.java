package com.johnfnash.learn.redis.hyperloglog.service;

import com.johnfnash.learn.redis.hyperloglog.redis.StringRedisHyperLogLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class BigDataDeduplicationService {

    @Autowired
    private StringRedisHyperLogLogService redisHyperLogLogService;

    private final String IP_EXTRACT_REGEX = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";

    private final Pattern pattern = Pattern.compile(IP_EXTRACT_REGEX);

    /**
     * IP地址去重统计
     * @param logFilePath
     */
    public void processLogFile(String logFilePath) {
        String key = "unique_ips:" + LocalDate.now();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(logFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String ip = extractIP(line);
                redisHyperLogLogService.pfAdd(key, ip);
            }
        } catch (IOException e) {
            log.error("处理日志文件失败", e);
        }
    }

    /**
     * 获取今日去重IP数
     */
    public Long getUniqueIPs() {
        String key = "unique_ips:" + LocalDate.now();
        long count = redisHyperLogLogService.pfCount(key);
        return count;
    }

    /**
     * 从日志行中提取IP地址
     */
    private String extractIP(String logLine) {
        // 从日志行中提取IP地址的逻辑
        Matcher matcher = pattern.matcher(logLine);
        return matcher.find() ? matcher.group(0) : null;
    }

}

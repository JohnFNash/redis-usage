package com.johnfash.learn.redis.bitmap.service;

import com.johnfash.learn.redis.bitmap.redis.StringRedisBitmapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IPAddressTracker {

    private static final String IP_ADDRESS_BLACK_KEY = "ip_address_black:";
    private static final String IP_VISIT_KEY = "ip_visit:";
    private static final String IP_DATE_VISIT_KEY = "ip_date_visit:";

    @Autowired
    private StringRedisBitmapService redisBitmapService;

    /**
     * 添加IP地址到黑名单
     * @param ipAddress
     */
    public void addToBlackList(String ipAddress) {
        redisBitmapService.setBit(getBlackListKey(ipAddress), getOffset(ipAddress), true);
    }

    /**
     * 判断IP地址是否在黑名单中
     * @param ipAddress
     * @return
     */
    public boolean isInBlackList(String ipAddress) {
        return redisBitmapService.getBit(getBlackListKey(ipAddress), getOffset(ipAddress));
    }

    /**
     * 构建IP地址黑名单的Key，ip前面两段拼到key中，大幅减少bitmap offset
     * @param ipAddress
     * @return
     */
    public String getBlackListKey(String ipAddress) {
        int secondPosition = ipAddress.indexOf(".", ipAddress.indexOf(".") + 1);
        return IP_ADDRESS_BLACK_KEY + ipAddress.substring(0, secondPosition);
    }

    /**
     * 记录IP访问
     */
    public void trackIPVisit(String ipAddress) {
        redisBitmapService.setBit(getTrackKey(ipAddress), getOffset(ipAddress), true);
    }

    /**
     * 获取不同IP访问总数
     */
    public long getIPVisitCount() {
        return redisBitmapService.bitCountWithShard(IP_VISIT_KEY);
    }

    /**
     * 构建IP地址访问的Key，ip前面两段拼到key中，大幅减少bitmap offset
     * @param ipAddress
     * @return
     */
    public String getTrackKey(String ipAddress) {
        // ip地址的前两段已经放到key里，大幅减少offset的值
        int secondPosition = ipAddress.indexOf(".", ipAddress.indexOf(".") + 1);
        return IP_VISIT_KEY + ipAddress.substring(0, secondPosition);
    }

    /**
     * 获取bitmap offset
     * @param ipAddress
     * @return
     */
    public long getOffset(String ipAddress) {
        // ip地址的前两段已经放到key里，这里将前两段置为0，大幅减少offset的值
        int secondPosition = ipAddress.indexOf(".", ipAddress.indexOf(".") + 1);
        String processedIP = "0.0." + ipAddress.substring(secondPosition + 1);
        return ipToLong(processedIP);
    }

    /**
     * 记录特定日期的IP访问
     */
    public void trackIPVisit(String ipAddress, LocalDate date) {
        redisBitmapService.setBit(getDateTrackKey(ipAddress, date), getOffset(ipAddress), true);
    }

    /**
     * 构建给定日期IP地址访问的Key，ip前面两段拼到key中，大幅减少bitmap offset
     * @param ipAddress
     * @return
     */
    public String getDateTrackKey(String ipAddress, LocalDate date) {
        // ip地址的前两段已经放到key里，大幅减少offset的值
        int secondPosition = ipAddress.indexOf(".", ipAddress.indexOf(".") + 1);
        return IP_DATE_VISIT_KEY + ipAddress.substring(0, secondPosition)  + ":" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 获取连续多天都活跃的IP数量
     */
    public long getContinuousIPCount(String[] dates) {
        if (dates == null || dates.length == 0) return 0;

        // 获取所有相关key
        List<String> keyList = redisBitmapService.scan(IP_DATE_VISIT_KEY);
        // 将key按照ip前两段进行分组
        Map<String, List<String>> shardMapList = keyList.stream().collect(Collectors.groupingBy(key -> {
            int index = key.indexOf(":", key.indexOf(".") + 1);
            return key.substring(0, index);
        }));

        // 将key按照ip前两段分别进行bitOpAnd 操作，然后加count累加
        long ipCount = 0;
        String tmpDestKey = "temp:date:active:ips:";
        for (String shardKey : shardMapList.keySet()) {
            List<String> tmpKeyList = shardMapList.get(shardKey);
            String[] tmpArr = new String[tmpKeyList.size()];
            tmpKeyList.toArray(tmpArr);

            String tmpIpKey = tmpDestKey + shardKey;
            redisBitmapService.bitOpAnd(tmpIpKey, tmpArr);
            ipCount += redisBitmapService.bitCount(tmpIpKey);

            // 删除临时key
            redisBitmapService.del(tmpIpKey);
        }

        return ipCount;
    }

    /**
     * IP地址转为长整型
     */
    public long ipToLong(String ipAddress) {
        try {
            byte[] bytes = InetAddress.getByName(ipAddress).getAddress();
            long result = 0;
            for (byte b : bytes) {
                result = result << 8 | (b & 0xFF);
            }
            return result;
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address: " + ipAddress, e);
        }
    }

    /**
     * 长整型转为IP地址
     */
    public String longToIp(long ip) {
        return ((ip >> 24) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                (ip & 0xFF);
    }

}

package com.johnfash.learn.redis.bitmap.controller;

import com.johnfash.learn.redis.bitmap.service.IPAddressTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/ip")
public class IPAddressTrackerController {

    @Autowired
    private IPAddressTracker ipAddressTracker;

    @GetMapping("/add")
    public String addToBlackList(String ipAddress) {
        ipAddressTracker.addToBlackList(ipAddress);
        return "success";
    }

    @GetMapping("/isInBlackList")
    public boolean isInBlackList(String ipAddress) {
        return ipAddressTracker.isInBlackList(ipAddress);
    }

    @GetMapping("/track")
    public String trackIPVisit(String ipAddress) {
        ipAddressTracker.trackIPVisit(ipAddress);
        return "success";
    }

    @GetMapping("/count")
    public long getIPVisitCount() {
        return ipAddressTracker.getIPVisitCount();
    }
    
    @GetMapping("/trackWithDate")
    public String trackIPVisit(String ipAddress, String date) {
        ipAddressTracker.trackIPVisit(ipAddress, LocalDate.parse(date));
        return "success";
    }
    
    @GetMapping("/continuous")
    public long getContinuousIPCount(String[] dates) {
        return ipAddressTracker.getContinuousIPCount(dates);
    }

}

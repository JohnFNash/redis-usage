package com.johnfash.learn.redis.bitmap.controller;

import com.johnfash.learn.redis.bitmap.service.DDOSProtection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ddos")
public class DDOSProtectionController {

    @Autowired
    private DDOSProtection ddosProtection;

    @RequestMapping("/shouldBlock")
    public boolean shouldBlockIP(String ipAddress, int accessLimit) {
        return ddosProtection.shouldBlockIP(ipAddress, accessLimit);
    }

}

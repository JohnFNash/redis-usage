package com.johnfnash.learn.redis.counter.controller;

import com.johnfnash.learn.redis.counter.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CounterController {

    @Autowired
    private CounterService counterService;

    @GetMapping("/access")
    public String access(@RequestParam(value = "id") String id) {
        counterService.add(id);
        return "访问成功";
    }

    @GetMapping("/get")
    public Integer get(@RequestParam(value = "id") String id) {
        return counterService.getKeyCounter(id);
    }

}

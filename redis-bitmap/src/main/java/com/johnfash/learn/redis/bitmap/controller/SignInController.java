package com.johnfash.learn.redis.bitmap.controller;

import com.johnfash.learn.redis.bitmap.service.SignInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/signin")
public class SignInController {

    @Autowired
    private SignInService signInService;

    @GetMapping("/sign")
    public String signIn(long userId, String date) {
        Boolean result = signInService.signIn(userId, LocalDate.parse(date));
        return result ? "success" : "fail";
    }

    @GetMapping("/count")
    public long getMonthlySignCount(long userId, String date) {
        return signInService.getMonthlySignCount(userId, LocalDate.parse(date));
    }

    @GetMapping("/first")
    public long getFirstSignInDay(long userId, String date) {
        return signInService.getFirstSignInDay(userId, LocalDate.parse(date));
    }

    @GetMapping("/days")
    public long getMonthlySignInDays(long userId, String date) {
        return signInService.getMonthlySignInDays(userId, LocalDate.parse(date));
    }

}

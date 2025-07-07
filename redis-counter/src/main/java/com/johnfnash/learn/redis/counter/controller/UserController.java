package com.johnfnash.learn.redis.counter.controller;

import com.johnfnash.learn.redis.counter.service.UserFollowService;
import com.johnfnash.learn.redis.counter.service.UserService;
import com.johnfnash.learn.redis.counter.vo.UserStatVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private UserFollowService userFollowService;

    @GetMapping("/add")
    public String addUser(@RequestParam(value = "username")String username) {
        userService.add(username, "初始密码");
        return "success";
    }

    @GetMapping("/addFollow")
    public String addUserFollow(@RequestParam(value = "userId")Long userId, @RequestParam(value = "followedUserId")Long followedUserId) {
        userFollowService.addUserFollow(userId, followedUserId);
        return "success";
    }

    @GetMapping("/cancelUserFollow")
    public String cancelUserFollow(@RequestParam(value = "userId")Long userId, @RequestParam(value = "followedUserId")Long followedUserId) {
        userFollowService.cancelUserFollow(userId, followedUserId);
        return "success";
    }

    @GetMapping("/getUserStat")
    public UserStatVo getUserStat(@RequestParam(value = "userId")Long userId) {
        return userService.getUserStat(userId);
    }

}

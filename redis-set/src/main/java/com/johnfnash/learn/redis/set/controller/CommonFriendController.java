package com.johnfnash.learn.redis.set.controller;

import com.johnfnash.learn.redis.set.service.CommonFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/friend")
public class CommonFriendController {

    @Autowired
    private CommonFriendService commonFriendService;

    @GetMapping("/add")
    public void addFriend(String userId, String friendId) {
        commonFriendService.addFriend(userId, friendId);
    }

    @GetMapping("/remove")
    public void removeFriend(String userId, String friendId) {
        commonFriendService.removeFriend(userId, friendId);
    }

    @GetMapping("/isFriend")
    public boolean isFriend(String userId, String friendId) {
        return commonFriendService.isFriend(userId, friendId);
    }

    @GetMapping("/count")
    public long getFriendCount(String userId) {
        return commonFriendService.getFriendCount(userId);
    }

    @GetMapping("/list")
    public Set<String> getFriendList(String userId) {
        return commonFriendService.getFriendList(userId);
    }

    @GetMapping("/commonList")
    public Set<String> getCommonFriendList(String userId1, String userId2) {
        return commonFriendService.getCommonFriendList(userId1, userId2);
    }

}

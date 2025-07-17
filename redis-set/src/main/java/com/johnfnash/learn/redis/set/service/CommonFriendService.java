package com.johnfnash.learn.redis.set.service;

import com.johnfnash.learn.redis.set.redis.StringRedisSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CommonFriendService {

    private static final String FRIEND_KEY = "friend:";

    @Autowired
    private StringRedisSetService redisSetService;

    public void addFriend(String userId, String friendId) {
        redisSetService.add(FRIEND_KEY + userId, friendId);
    }

    public void removeFriend(String userId, String friendId) {
        redisSetService.remove(FRIEND_KEY + userId, friendId);
    }

    public boolean isFriend(String userId, String friendId) {
        return redisSetService.exists(FRIEND_KEY + userId, friendId);
    }

    public long getFriendCount(String userId) {
        return redisSetService.size(FRIEND_KEY + userId);
    }

    public Set<String> getFriendList(String userId) {
        return redisSetService.getAll(FRIEND_KEY + userId);
    }

    public Set<String> getCommonFriendList(String userId1, String userId2) {
        return redisSetService.intersect(FRIEND_KEY + userId1, FRIEND_KEY + userId2);
    }

}

package com.johnfnash.learn.redis.zset.controller;

import com.johnfnash.learn.redis.zset.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/rank")
public class RankController {

    @Autowired
    private RankService rankService;

    // 添加用户得分
    @GetMapping("/add")
    public ResponseEntity<String> addUser(@RequestParam("userId") String userId, @RequestParam("score") int score) {
        rankService.addUserToLeaderboard(userId, score, true);
        return ResponseEntity.ok("User added to leaderboard.");
    }

    // 获取前N名
    @GetMapping("/top")
    public ResponseEntity<Set<String>> getTopUsers(@RequestParam("topN") int topN) {
        return ResponseEntity.ok(rankService.getTopUsers(topN, true));
    }

    // 获取某个用户的排名和得分
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserRankAndScore(@PathVariable String userId) {
        return ResponseEntity.ok(rankService.getUserRankAndScore(userId, true));
    }

}

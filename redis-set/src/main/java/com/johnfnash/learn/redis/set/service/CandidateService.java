package com.johnfnash.learn.redis.set.service;

import com.johnfnash.learn.redis.set.redis.StringRedisSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CandidateService {

    private final String CANDIDATE_KEY = "candidate:";

    @Autowired
    private StringRedisSetService redisSetService;
    @Resource(name = "redisRawTemplate")
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 初始化候选集
     * @param candidateIdList
     */
    public void init(List<String> candidateIdList) {
        if (CollectionUtils.isEmpty(candidateIdList)) {
            return;
        }

        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            for (String candidateId : candidateIdList) {
                redisSetService.add(CANDIDATE_KEY, candidateId);
            }
            return null;
        });
    }

    /****
     * 抽奖
     *
     * @param count
     * @return
     */
    public List<String> candidate(int count) {
        return redisSetService.pop(CANDIDATE_KEY, count);
    }

}

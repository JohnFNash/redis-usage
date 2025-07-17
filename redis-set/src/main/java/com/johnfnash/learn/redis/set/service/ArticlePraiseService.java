package com.johnfnash.learn.redis.set.service;

import com.johnfnash.learn.redis.set.redis.StringRedisSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ArticlePraiseService {

    private static final String PRAISE_KEY = "article:praise:";

    @Autowired
    private StringRedisSetService redisSetService;

    /**
     * 添加点赞
     * @param userId
     * @param articleId
     */
    public void addPraise(String userId, String articleId) {
        redisSetService.add(PRAISE_KEY + articleId, userId);
    }

    /**
     * 取消点赞
     * @param userId
     * @param articleId
     */
    public void removePraise(String userId, String articleId) {
        redisSetService.remove(PRAISE_KEY + articleId, userId);
    }

    /**
     * 获取点赞数
     * @param articleId
     * @return
     */
    public long getPraiseCount(String articleId) {
        return redisSetService.size(PRAISE_KEY + articleId);
    }

    /**
     * 获取文章的点赞用户列表
     * @param articleId
     * @return
     */
    public Set<String> getPraiseUserList(String articleId) {
        return redisSetService.getAll(PRAISE_KEY + articleId);
    }

}

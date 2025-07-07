package com.johnfnash.learn.redis.counter.service;

import com.johnfnash.learn.redis.counter.kafka.KafkaConfig;
import com.johnfnash.learn.redis.counter.kafka.service.KafkaProducerService;
import com.johnfnash.learn.redis.counter.kafka.vo.ArticleCollectVo;
import com.johnfnash.learn.redis.counter.kafka.vo.KafkaDTOInterface;
import com.johnfnash.learn.redis.counter.kafka.vo.UserFollowVo;
import com.johnfnash.learn.redis.counter.redis.util.Constants;
import com.johnfnash.learn.redis.counter.redis.util.StringRedisHashService;
import com.johnfnash.learn.redis.counter.redis.util.StringRedisZSetService;
import com.johnfnash.learn.redis.counter.vo.RedisPipelineReq;
import com.johnfnash.learn.redis.counter.vo.StatVo;
import com.johnfnash.learn.redis.counter.vo.UserStatVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotifyMsgService {

    @Autowired
    private StringRedisHashService stringRedisHashService;
    @Autowired
    private StringRedisZSetService stringRedisZSetService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private KafkaProducerService kafkaProducerService;
    @Resource(name = "redisRawTemplate")
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 添加/取消用户关注
     * @param userId
     * @param followedUserId
     * @param num 变化数量
     */
    public boolean addUserFollow(Long userId, Long followedUserId, Long num) {
        RLock lock = redissonClient.getLock(Constants.DISTRIBUTED_LOCK_BY_USER_KEY + userId);
        try {
            // 尝试加锁，最多等待10秒，上锁以后10秒自动解锁
            if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                try {
                    // 添加用户关注
                    if (num > 0) {
                        // 添加到用户的关注用户集合
                        boolean added = stringRedisZSetService.addIfAbsent(Constants.USER_FOLLOW_LIST_KEY + userId, String.valueOf(followedUserId));
                        if (!added) {
                            return false;
                        }
                    } else {
                        // 删除用户关注
                        stringRedisZSetService.remove(Constants.USER_FOLLOW_LIST_KEY + userId, String.valueOf(followedUserId));
                    }
                    // 当前用户增加关注用户
                    Long totalFollows = stringRedisHashService.increment(Constants.USER_STATS_COUNT_HASH_KEY + userId, Constants.USER_FOLLOW_KEY, num);
                    // 被关注用户增加粉丝
                    Long totalFans = stringRedisHashService.increment(Constants.USER_STATS_COUNT_HASH_KEY + followedUserId, Constants.USER_FANS_KEY, num);

                    // 发送消息到kafka保存关注明细
                    KafkaDTOInterface data = UserFollowVo.of(userId, followedUserId, num > 0, totalFans, totalFollows);
                    kafkaProducerService.sendMessageAsync(KafkaConfig.USER_FOLLOW_TOPIC, data);
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("添加/取消用户关注报错", e);
            return false;
        }
        return true;
    }

    /**
     * 设置文章收藏
     * @param userId
     * @param articleId
     * @param num
     */
    public boolean addArticleCollection(Long userId, Long articleId, long num) {
        RLock lock = redissonClient.getLock(Constants.DISTRIBUTED_LOCK_BY_USER_KEY + userId);
        try {
            // 尝试加锁，最多等待10秒，上锁以后10秒自动解锁
            if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                try {
                    if (num > 0) {
                        // 添加到用户的收藏文章集合
                        boolean added = stringRedisZSetService.addIfAbsent(Constants.USER_COLLECT_ARTICLE_LIST_KEY + userId, String.valueOf(articleId));
                        if (!added) {
                            return false;
                        }
                    } else {
                        // 从用户的收藏文章集合里移除
                        stringRedisZSetService.remove(Constants.USER_COLLECT_ARTICLE_LIST_KEY + userId, String.valueOf(articleId));
                    }
                    // 用户文章收藏数+1/-1
                    stringRedisHashService.increment(Constants.USER_STATS_COUNT_HASH_KEY + userId, Constants.ARTICLE_COLLECT_KEY, num);

                    // 发送消息到kafka保存关注明细
                    KafkaDTOInterface data = ArticleCollectVo.of(userId, articleId, num > 0);
                    kafkaProducerService.sendMessageAsync(KafkaConfig.ARTICLE_COLLECT_TOPIC, data);
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("添加/取消用户关注报错", e);
            return false;
        }
        return true;
    }

    /**
     * 添加文章阅读数，包括单篇文章+用户下所有文章的
     * @param userId
     * @param articleId
     */
    public boolean addArticleReadCount(Long userId, Long articleId) {
        RLock lock = redissonClient.getLock(Constants.DISTRIBUTED_LOCK_BY_USER_KEY + userId);
        try {
            // 尝试加锁，最多等待10秒，上锁以后10秒自动解锁
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                try {
                    // 用户文章阅读数+1
                    stringRedisHashService.incrementOne(Constants.USER_STATS_COUNT_HASH_KEY + userId, Constants.USER_ARTICLE_READ_KEY);
                    // 文章阅读数+1
                    stringRedisHashService.incrementOne(Constants.ARTICLE_STAT_COUNT_HASH_KEY + articleId, Constants.ARTICLE_READ_KEY);
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("添加文章阅读数报错", e);
            return false;
        }
        return true;
    }

    /**
     * 批量获取文章阅读数
     * @param keyList
     * @return
     */
    public Map<String, String> getArticleReadBatch(List<String> keyList) {
        if (CollectionUtils.isEmpty(keyList)) {
            return Collections.emptyMap();
        }

        // key: articleId，value: 用户当前文章累计被阅读数
        Map<String, String> resultMap = new HashMap<>(keyList.size());
        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            for (String key : keyList) {
                String value = stringRedisHashService.get(key, Constants.ARTICLE_READ_KEY);
                resultMap.put(key.replace(Constants.ARTICLE_STAT_COUNT_HASH_KEY, ""), value);
            }
            return null;
        });
        return resultMap;
    }

    /**
     * 批量设置文章数
     * @param statList
     */
    public void setArticleCountBatch(List<StatVo> statList) {
        if (CollectionUtils.isEmpty(statList)) {
            return;
        }

        List<RedisPipelineReq> reqList = statList.stream().map(stat -> {
            RedisPipelineReq req = new RedisPipelineReq();
            req.setKey(Constants.USER_STATS_COUNT_HASH_KEY + stat.getBizId());
            req.setHkey(Constants.USER_ARTICLE_KEY);
            req.setValue(String.valueOf(stat.getNum()));
            return req;
        }).collect(Collectors.toList());
        stringRedisHashService.executeHashPipeline(reqList);
    }

    /**
     * 批量获取用户关注明细列表
     * @param keyList
     * @return
     */
    public Map<String, Set<String>> getUserFollowBatch(List<String> keyList) {
        if (CollectionUtils.isEmpty(keyList)) {
            return Collections.emptyMap();
        }

        // key: userId，value: 用户关注的用户列表
        Map<String, Set<String>> resultMap = new HashMap<>(keyList.size());
        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            for (String key : keyList) {
                Set<String> allValues = stringRedisZSetService.getAll(key);
                resultMap.put(key.replace(Constants.USER_FOLLOW_LIST_KEY, ""), allValues);
            }
            return null;
        });
        return resultMap;
    }

    /**
     * 批量获取用户文章收藏明细列表
     * @param keyList
     * @return
     */
    public Map<String, Set<String>> getArticleCollectBatch(List<String> keyList) {
        if (CollectionUtils.isEmpty(keyList)) {
            return Collections.emptyMap();
        }

        // key: userId，value: 用户关注的用户列表
        Map<String, Set<String>> resultMap = new HashMap<>(keyList.size());
        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            for (String key : keyList) {
                Set<String> allValues = stringRedisZSetService.getAll(key);
                resultMap.put(key.replace(Constants.USER_COLLECT_ARTICLE_LIST_KEY, ""), allValues);
            }
            return null;
        });
        return resultMap;
    }

    /**
     * 获取用户统计信息
     * @param userId
     * @return
     */
    public UserStatVo getUserStat(Long userId) {
        UserStatVo vo = new UserStatVo();
        vo.setUserId(userId);

        // 用户文章数
        Map<String, String> userStatMap =
                stringRedisHashService.getAll(Constants.USER_STATS_COUNT_HASH_KEY + userId);
        // 用户文章数
        vo.setArticleCount(userStatMap.getOrDefault(Constants.USER_ARTICLE_KEY, "0"));
        // 用户文章阅读数
        vo.setArticleReadCount(userStatMap.getOrDefault(Constants.USER_ARTICLE_READ_KEY, "0"));
        // 用户文章收藏数
        vo.setArticleCollectionCount(userStatMap.getOrDefault(Constants.ARTICLE_COLLECT_KEY, "0"));
        // 用户关注数
        vo.setFollowCount(userStatMap.getOrDefault(Constants.USER_FOLLOW_KEY, "0"));
        // 用户粉丝数
        vo.setFollowerCount(userStatMap.getOrDefault(Constants.USER_FANS_KEY, "0"));

        return vo;
    }

}

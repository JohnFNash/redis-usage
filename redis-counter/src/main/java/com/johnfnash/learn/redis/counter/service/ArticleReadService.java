package com.johnfnash.learn.redis.counter.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.johnfnash.learn.redis.counter.entity.ArticleRead;
import com.johnfnash.learn.redis.counter.mapper.ArticleReadMapper;
import com.johnfnash.learn.redis.counter.redis.util.Constants;
import com.johnfnash.learn.redis.counter.redis.util.StringRedisHashService;
import com.johnfnash.learn.redis.counter.util.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文章阅读记录表(ArticleRead)表服务实现类
 */
@Slf4j
@Service("articleReadService")
public class ArticleReadService extends ServiceImpl<ArticleReadMapper, ArticleRead> {

    @Autowired
    private NotifyMsgService notifyMsgService;
    @Autowired
    private StringRedisHashService stringRedisHashService;

    public void add(Long userId, Long articleId) {
        boolean added = notifyMsgService.addArticleReadCount(userId, articleId);
        if (!added) {
            log.error("文章阅读次数更新失败, {}, {}", userId, articleId);
        }
    }

    /**
     * 全量同步文章阅读记录表数据到DB
     */
    public void fullSyncArticleReadToDB() {
        Function<List<String>, Map<String, String>> getValueFunction = keyList -> {
            Map<String, String> valueMap = notifyMsgService.getArticleReadBatch(keyList);
            return valueMap;
        };
        Function<Map<String, String>, Void> processFunction = map -> {
            SpringContextHolder.getBean(ArticleReadService.class).syncArticleReadToDB(map);
            return null;
        };
        stringRedisHashService.hashScanBatch(Constants.ARTICLE_STAT_COUNT_HASH_KEY, getValueFunction, processFunction);
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncArticleReadToDB(Map<String, String> map) {
        if (CollectionUtils.isEmpty(map)) {
            return;
        }

        List<String> articleIdList = map.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
        List<String> existsArticleIdList = baseMapper.selectBatchByArticleIdList(articleIdList);

        // 批量更新
        List<ArticleRead> updateList = map.entrySet().stream()
                .filter(entry -> existsArticleIdList.contains(entry.getKey()))
                .map(entry -> {
                    ArticleRead articleRead = new ArticleRead();
                    articleRead.setArticleId(Long.valueOf(entry.getKey()));
                    articleRead.setTotalReadCount(Long.valueOf(entry.getValue().toString()));
                    return articleRead;
                }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(updateList)) {
            baseMapper.updateCountBatch(updateList);
        }

        // 批量插入
        Date now = new Date();
        List<ArticleRead> insertList = map.entrySet().stream()
                .filter(entry -> !existsArticleIdList.contains(entry.getKey()))
                .map(entry -> {
                    ArticleRead articleRead = new ArticleRead();
                    articleRead.setArticleId(Long.valueOf(entry.getKey()));
                    articleRead.setTotalReadCount(Long.valueOf(entry.getValue().toString()));
                    articleRead.setCreateTime(now);
                    articleRead.setUpdateTime(now);
                    return articleRead;
                }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(insertList)) {
            saveBatch(insertList);
        }
    }

}

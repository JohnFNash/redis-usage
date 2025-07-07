package com.johnfnash.learn.redis.counter.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.johnfnash.learn.redis.counter.entity.ArticleCollection;
import com.johnfnash.learn.redis.counter.kafka.vo.ArticleCollectVo;
import com.johnfnash.learn.redis.counter.mapper.ArticleCollectionMapper;
import com.johnfnash.learn.redis.counter.redis.util.Constants;
import com.johnfnash.learn.redis.counter.redis.util.StringRedisZSetService;
import com.johnfnash.learn.redis.counter.util.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文章收藏记录表(ArticleCollection)表服务实现类
 */
@Service("articleCollectionService")
@Transactional(readOnly = true)
public class ArticleCollectionService extends ServiceImpl<ArticleCollectionMapper, ArticleCollection> {

    @Autowired
    private NotifyMsgService notifyMsgService;
    @Autowired
    private StringRedisZSetService stringRedisZSetService;

    public void add(Long articleId, Long userId) {
        boolean added = notifyMsgService.addArticleCollection(userId, articleId, 1);
        if (!added) {
            throw new RuntimeException("文章添加收藏失败，请稍后重试");
        }
    }

    public void delete(Long articleId, Long userId) {
        boolean added = notifyMsgService.addArticleCollection(userId, articleId, -1);
        if (!added) {
            throw new RuntimeException("文章取消收藏失败，请稍后重试");
        }
    }

    /**
     * 同步文章收藏记录到数据库
     */
    @Transactional
    public void syncArticleCollectionToDB(List<ArticleCollectVo> voList) {
        if (CollectionUtils.isEmpty(voList)) {
            return;
        }

        // 再次确认数据库是否存在记录
        List<ArticleCollection> dbList = baseMapper.selectBatch(voList);
        Map<String, ArticleCollection> dbMap =
                dbList.stream().collect(Collectors.toMap(a -> {
                    return a.getUserId() + ":" + a.getArticleId();
                }, v -> v));

        // 批量添加文章收藏记录
        List<ArticleCollectVo> addList = voList.stream().filter(vo -> vo.isAdd())
                .filter(vo -> !dbMap.containsKey(vo.getUserId() + ":" + vo.getArticleId())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(addList)) {
            Date now = new Date();
            List<ArticleCollection> list = addList.stream().map(vo -> {
                ArticleCollection articleCollection = new ArticleCollection();
                articleCollection.setUserId(vo.getUserId());
                articleCollection.setArticleId(vo.getArticleId());
                articleCollection.setCreateTime(now);
                return articleCollection;
            }).collect(Collectors.toList());
            saveBatch(list);
        }

        // 批量取消文章收藏记录
        List<Long> deleteIdList = voList.stream().filter(vo -> !vo.isAdd())
                .filter(vo -> dbMap.containsKey(vo.getUserId() + ":" + vo.getArticleId()))
                .map(vo -> dbMap.get(vo.getUserId() + ":" + vo.getArticleId()).getId()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteIdList)) {
            baseMapper.deleteBatchIds(deleteIdList);
        }
    }

    @Transactional
    public void syncArticleCollectionToDB(Map<String, Set<String>> map) {
        if (CollectionUtils.isEmpty(map)) {
            return;
        }

        List<ArticleCollectVo> voList = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            for (String obj : entry.getValue()) {
                ArticleCollectVo vo = new ArticleCollectVo();
                vo.setUserId(Long.valueOf(key));
                vo.setArticleId(Long.valueOf(obj));
                vo.setAdd(true);
                voList.add(vo);
            }
        }
        SpringContextHolder.getBean(ArticleCollectionService.class).syncArticleCollectionToDB(voList);
    }

    /**
     * 全量同步文章收藏记录表数据到DB
     */
    public void fullSyncArticleCollectionToDB() {
        Function<List<String>, Map<String, Set<String>>> getValueFunction = keyList -> {
            Map<String, Set<String>> valueMap = notifyMsgService.getArticleCollectBatch(keyList);
            return valueMap;
        };
        Function<Map<String, Set<String>>, Void> processFunction = map -> {
            SpringContextHolder.getBean(ArticleCollectionService.class).syncArticleCollectionToDB(map);
            return null;
        };
        stringRedisZSetService.zSetScanBatch(Constants.ARTICLE_READ_KEY, getValueFunction, processFunction);
    }

}

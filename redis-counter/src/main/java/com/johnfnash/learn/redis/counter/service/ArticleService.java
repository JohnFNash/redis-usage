package com.johnfnash.learn.redis.counter.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.johnfnash.learn.redis.counter.vo.StatVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.johnfnash.learn.redis.counter.mapper.ArticleMapper;
import com.johnfnash.learn.redis.counter.entity.Article;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 文章信息表(Article)表服务实现类
 */
@Service("articleService")
@Transactional(readOnly = true)
public class ArticleService extends ServiceImpl<ArticleMapper, Article> {

    @Autowired
    private NotifyMsgService notifyMsgService;

    @Transactional
    public void add(String title, String content, Long userId) {
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setUserId(userId);
        article.setStatus(1);
        Date now = new Date();
        article.setCreateTime(now);
        article.setUpdateTime(now);
        save(article);

        // 不使用下面的方式：Redis 失败影响一致性
        // 更新redis
        //notifyMsgService.setArticleCount(userId, 1);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        // TODO：检查文章是不是用户的

        baseMapper.updateStatus(id, 2);

        // 不使用下面的方式：Redis 失败影响一致性
        // 更新redis
        //notifyMsgService.setArticleCount(userId, -1);
    }

    /**
     * 批量更新文章数量到redis
     * @param updateTime
     * @return
     */
    public boolean updateArticleCntToRedis(Date updateTime) {
        int pageNo = 1;
        int pageSize = 500;
        List<Long> userIdList;
        List<StatVo> statList;
        try {
            do {
                userIdList = baseMapper.getUpdatedArticleUserIdList(updateTime, new Page<>(pageNo, pageSize));
                if (CollectionUtils.isEmpty(userIdList)) {
                    break;
                }

                statList = baseMapper.countArticleByUserIdList(userIdList);
                notifyMsgService.setArticleCountBatch(statList);

                pageNo++;
            } while (CollectionUtils.isNotEmpty(userIdList));
        } catch (Exception e) {
            log.error("刷新文章数量到redis报错", e);
            return false;
        }

        return true;
    }

}

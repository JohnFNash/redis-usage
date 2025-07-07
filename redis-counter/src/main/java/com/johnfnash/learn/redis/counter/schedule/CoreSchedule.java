package com.johnfnash.learn.redis.counter.schedule;

import com.johnfnash.learn.redis.counter.service.ArticleCollectionService;
import com.johnfnash.learn.redis.counter.service.ArticleReadService;
import com.johnfnash.learn.redis.counter.service.ArticleService;
import com.johnfnash.learn.redis.counter.service.UserFollowService;
import com.johnfnash.learn.redis.counter.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class CoreSchedule {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleReadService articleReadService;
    @Autowired
    private UserFollowService userFollowService;
    @Autowired
    private ArticleCollectionService articleCollectionService;

    @Scheduled(cron = "${schedule.userArticleCount.cron:0 0/5 * * * ?}")
    public void syncUserArticleCountToRedis() {
        Date date = DateUtil.addMinute(-120);
        articleService.updateArticleCntToRedis(date);
    }

    @Scheduled(cron = "${schedule.articleReadCount.cron:0 0 0/2 * * ?}")
    public void syncArticleReadCountToDB() {
        articleReadService.fullSyncArticleReadToDB();
    }

    @Scheduled(cron = "${schedule.articleCollectionList.cron:0 0 2 * * ?}")
    public void syncArticleCollectionListToDB() {
        articleCollectionService.fullSyncArticleCollectionToDB();
    }

    @Scheduled(cron = "${schedule.userFollowList.cron:0 0 3 * * ?}")
    public void syncUserFollowListToDB() {
        userFollowService.fullSyncUserFollowToDB();
    }

}

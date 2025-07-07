package com.johnfnash.learn.redis.counter.redis.util;

public class Constants {

    /**
     * 用户相关统计数。user:stats:{userId} {follow, fans, article, praise, read, collect}。使用hash减少 Key 数量
     */
    public static final String USER_STATS_COUNT_HASH_KEY = "u:s:c:h:";

    /**
     * 用户关注
     */
    public static final String USER_FOLLOW_KEY = "fo";

    /**
     * 用户粉丝
     */
    public static final String USER_FANS_KEY = "fa";

    /**
     * 用户文章数
     */
    public static final String USER_ARTICLE_KEY = "ar:cnt";

    /**
     * 用户所有文章累计被阅读数
     */
    public static final String USER_ARTICLE_READ_KEY = "u:ar:rd";

    /**
     * 文章相关统计数key
     */
    public static final String ARTICLE_STAT_COUNT_HASH_KEY = "ar:stat:c:";

    /**
     * 用户某偏文章被阅读数
     */
    public static final String ARTICLE_READ_KEY = "ar:rd";

    /**
     * 用户某偏文章被收藏数
     */
    public static final String ARTICLE_COLLECT_KEY = "ar:cl";

    /**
     * 用户关注集合
     */
    public static final String USER_FOLLOW_LIST_KEY = "u:fo:l";

    /**
     * 用户收藏文章集合
     */
    public static final String USER_COLLECT_ARTICLE_LIST_KEY = "u:cl:a:l";

    /**
     * 分布式锁按用户级别key前缀
     */
    public static final String DISTRIBUTED_LOCK_BY_USER_KEY = "p:lo:u:";

    /**
     * 全局分布式锁key前缀
     */
    public static final String DISTRIBUTED_GLOBAL_LOCK_KEY = "p:lo:g";

}

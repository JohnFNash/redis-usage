package com.johnfnash.learn.redis.counter.mapper;

import com.johnfnash.learn.redis.counter.entity.ArticleRead;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章阅读记录表(ArticleRead)表数据库访问层
 */
@Mapper
public interface ArticleReadMapper extends BaseMapper<ArticleRead> {

    List<String> selectBatchByArticleIdList(@Param("list") List<String> articleIdList);

    void updateCountBatch(@Param("list") List<ArticleRead> list);

}

package com.johnfnash.learn.redis.counter.mapper;

import com.johnfnash.learn.redis.counter.entity.ArticleCollection;
import com.johnfnash.learn.redis.counter.kafka.vo.ArticleCollectVo;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章收藏记录表(ArticleCollection)表数据库访问层
 */
@Mapper
public interface ArticleCollectionMapper extends BaseMapper<ArticleCollection> {

    List<ArticleCollection> selectBatch(@Param("list") List<ArticleCollectVo> list);

}

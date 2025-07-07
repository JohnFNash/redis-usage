package com.johnfnash.learn.redis.counter.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.johnfnash.learn.redis.counter.vo.StatVo;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.johnfnash.learn.redis.counter.entity.Article;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 文章信息表(Article)表数据库访问层
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    @Update("update article set status = #{status}, update_time = sysdate where id = #{id}")
    void updateStatus(@Param(value = "id") Long id, @Param(value = "status") Integer status);

    List<Long> getUpdatedArticleUserIdList(@Param(value = "updateTime") Date updateTime, Page<Long> page);

    List<StatVo> countArticleByUserIdList(@Param(value = "list") List<Long> userIdList);

}

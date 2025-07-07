package com.johnfnash.learn.redis.counter.mapper;

import com.johnfnash.learn.redis.counter.entity.UserFollow;
import com.johnfnash.learn.redis.counter.kafka.vo.UserFollowVo;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户关注关系表(UserFollow)表数据库访问层
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {

    List<UserFollow> selectBatch(@Param("list") List<UserFollowVo> voList);

}

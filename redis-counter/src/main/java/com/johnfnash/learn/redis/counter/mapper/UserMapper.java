package com.johnfnash.learn.redis.counter.mapper;

import com.johnfnash.learn.redis.counter.entity.User;
import com.johnfnash.learn.redis.counter.kafka.vo.UserFollowVo;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户基础信息表(User)表数据库访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    //@Update("update user set follow_count = follow_count + 1 where id = #{id}")
    //void incrFollowCount(@Param(value = "id") Long id);

    //@Update("update user set fans_count = fans_count + 1 where id = #{id}")
    //void incrFansCount(@Param(value = "id") Long id);

    void updateFollowCountBatch(@Param("list") List<UserFollowVo> list);

    void updateFansCountBatch(@Param("list") List<UserFollowVo> list);

}

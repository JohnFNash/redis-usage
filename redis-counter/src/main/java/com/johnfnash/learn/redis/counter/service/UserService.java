package com.johnfnash.learn.redis.counter.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.johnfnash.learn.redis.counter.entity.User;
import com.johnfnash.learn.redis.counter.kafka.vo.UserFollowVo;
import com.johnfnash.learn.redis.counter.mapper.UserMapper;
import com.johnfnash.learn.redis.counter.vo.UserStatVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 用户基础信息表(User)表服务实现类
 */
@Service("userService")
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private NotifyMsgService notifyMsgService;

    @Transactional
    public void add(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFollowCount(0);
        user.setFansCount(0);
        user.setArticleCount(0);
        user.setCreateTime(new java.util.Date());
        user.setUpdateTime(new java.util.Date());
        this.save(user);
    }

    @Transactional
    public void updateFollowCountBatch(List<UserFollowVo> voList) {
        if (CollectionUtils.isEmpty(voList)) {
            return;
        }
        baseMapper.updateFollowCountBatch(voList);
        baseMapper.updateFansCountBatch(voList);
    }

    /**
     * 获取用户统计信息
     * @param userId
     * @return
     */
    public UserStatVo getUserStat(Long userId) {
        return notifyMsgService.getUserStat(userId);
    }

}

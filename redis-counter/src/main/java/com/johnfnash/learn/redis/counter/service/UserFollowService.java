package com.johnfnash.learn.redis.counter.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.johnfnash.learn.redis.counter.entity.UserFollow;
import com.johnfnash.learn.redis.counter.kafka.vo.UserFollowVo;
import com.johnfnash.learn.redis.counter.mapper.UserFollowMapper;
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
 * 用户关注关系表(UserFollow)表服务实现类
 */
@Service("userFollowService")
public class UserFollowService extends ServiceImpl<UserFollowMapper, UserFollow> {

    @Autowired
    private UserService userService;
    @Autowired
    private NotifyMsgService notifyMsgService;
    @Autowired
    private StringRedisZSetService stringRedisZSetService;

    /**
     * 添加关注关系
     * @param userId
     * @param followedUserId
     */
    public void addUserFollow(Long userId, Long followedUserId) {
        boolean added = notifyMsgService.addUserFollow(userId, followedUserId, 1L);
        if (!added) {
            throw new RuntimeException("添加关注关系失败，请稍后重试");
        }
    }

    /**
     * 取消关注关系
     * @param userId
     * @param followedUserId
     */
    public void cancelUserFollow(Long userId, Long followedUserId) {
        boolean added = notifyMsgService.addUserFollow(userId, followedUserId, -1L);
        if (!added) {
            throw new RuntimeException("取消关注关系失败，请稍后重试");
        }
    }

    /**
     * 批量保存用户关注关系，以及更新用户关注数（当updateDetailList为false时更新用户关注数）
     * @param voList
     * @param shouldUpdateUserFollowCnt 是否需要更新用户关注数
     */
    @Transactional
    public void saveUserFollowBatch(List<UserFollowVo> voList, boolean shouldUpdateUserFollowCnt) {
        if (CollectionUtils.isEmpty(voList)) {
            return;
        }

        // 查询数据库中已存在的关注关系，避免重复添加或删除
        List<UserFollow> dbList = baseMapper.selectBatch(voList);
        Map<String, UserFollow> dbMap =
                dbList.stream().collect(Collectors.toMap(a -> {
                    return a.getUserId() + ":" + a.getFollowedUserId();
                }, v -> v));

        // 批量添加关注关系
        List<UserFollowVo> addList = voList.stream().filter(vo -> vo.isAdd())
                .filter(vo -> !dbMap.containsKey(vo.getUserId() + ":" + vo.getFollowedUserId())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(addList)) {
            Date now = new Date();
            List<UserFollow> list = addList.stream().map(vo -> {
                UserFollow userFollow = new UserFollow();
                userFollow.setUserId(vo.getUserId());
                userFollow.setFollowedUserId(vo.getFollowedUserId());
                userFollow.setCreateTime(now);
                return userFollow;
            }).collect(Collectors.toList());
            saveBatch(list);
        }

        // 批量取消关注关系
        List<UserFollowVo> deleteList = voList.stream().filter(vo -> !vo.isAdd())
                .filter(vo -> dbMap.containsKey(vo.getUserId() + ":" + vo.getFollowedUserId())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteList)) {
            baseMapper.selectBatch(deleteList);
        }

        // 更新关注数
        if (shouldUpdateUserFollowCnt) {
            userService.updateFollowCountBatch(voList);
        }
    }


    @Transactional
    public void syncUserFollowToDB(Map<String, Set<String>> map) {
        if (CollectionUtils.isEmpty(map)) {
            return;
        }

        List<UserFollowVo> voList = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            for (String obj : entry.getValue()) {
                UserFollowVo vo = new UserFollowVo();
                vo.setUserId(Long.valueOf(key));
                vo.setFollowedUserId(Long.valueOf(obj));
                vo.setAdd(true);
                voList.add(vo);
            }
        }
        SpringContextHolder.getBean(UserFollowService.class).saveUserFollowBatch(voList, false);
    }

    /**
     * 全量同步用户关注记录表数据到DB
     */
    public void fullSyncUserFollowToDB() {
        Function<List<String>, Map<String, Set<String>>> getValueFunction = keyList -> {
            Map<String, Set<String>> valueMap = notifyMsgService.getUserFollowBatch(keyList);
            return valueMap;
        };
        Function<Map<String, Set<String>>, Void> processFunction = map -> {
            SpringContextHolder.getBean(UserFollowService.class).syncUserFollowToDB(map);
            return null;
        };
        stringRedisZSetService.zSetScanBatch(Constants.USER_FOLLOW_LIST_KEY, getValueFunction, processFunction);
    }

}
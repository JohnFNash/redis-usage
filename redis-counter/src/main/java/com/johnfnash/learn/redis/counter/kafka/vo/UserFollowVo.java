package com.johnfnash.learn.redis.counter.kafka.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFollowVo implements KafkaDTOInterface {

    private boolean add;

    private Long userId;

    private Long followedUserId;

    /**
     * 被关注的用户的总粉丝数
     */
    private Long totalFans;

    /**
     * 发起关注的用户的总关注数
     */
    private Long totalFollows;

    public static UserFollowVo of(Long userId, Long followedUserId, boolean add, Long totalFans, Long totalFollows) {
    	UserFollowVo vo = new UserFollowVo();
    	vo.setUserId(userId);
    	vo.setFollowedUserId(followedUserId);
    	vo.setAdd(add);
        vo.setTotalFans(totalFans);
        vo.setTotalFollows(totalFollows);
    	return vo;
    }

}

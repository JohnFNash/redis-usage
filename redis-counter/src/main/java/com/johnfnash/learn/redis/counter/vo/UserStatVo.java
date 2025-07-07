package com.johnfnash.learn.redis.counter.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatVo {

    private Long userId;

    private String articleCount;

    private String articleReadCount;

    private String articleCollectionCount;

    private String followCount;

    private String followerCount;

}

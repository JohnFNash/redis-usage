package com.johnfnash.learn.redis.counter.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisPipelineReq {

    private String key;

    private String hkey;

    private String value;

}

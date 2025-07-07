package com.johnfnash.learn.redis.counter.redis.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ZSetRangeBatchReq {

    private List<String> keyList;

    private double min;

    private double max;

    public static ZSetRangeBatchReq build(List<String> keyList, double min, double max) {
    	ZSetRangeBatchReq req = new ZSetRangeBatchReq();
    	req.setKeyList(keyList);
    	req.setMin(min);
    	req.setMax(max);
    	return req;
    }

}

package com.johnfnash.learn.redis.counter.kafka.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleCollectVo implements KafkaDTOInterface {

    private boolean add;

    private Long userId;

    private Long articleId;

    public static ArticleCollectVo of(Long userId, Long articleId, boolean add) {
    	ArticleCollectVo vo = new ArticleCollectVo();
    	vo.setUserId(userId);
    	vo.setArticleId(articleId);
    	vo.setAdd(add);
    	return vo;
    }

}

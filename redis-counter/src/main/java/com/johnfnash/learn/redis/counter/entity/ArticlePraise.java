package com.johnfnash.learn.redis.counter.entity;

import java.util.Date;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * 文章点赞记录表(ArticlePraise)表实体类
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@TableName("article_praise")
public class ArticlePraise implements Serializable {
    private static final long serialVersionUID = -27930306108783635L;

    //主键ID    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    //文章ID    
    private Long articleId;

    //点赞用户ID    
    private Long userId;

    //创建时间    
    private Date createTime;

}


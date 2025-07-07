package com.johnfnash.learn.redis.counter.entity;

import java.util.Date;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * 文章收藏记录表(ArticleCollection)表实体类
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@TableName("article_collection")
public class ArticleCollection implements Serializable {
    private static final long serialVersionUID = -14436779711286469L;

    //主键ID    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    //文章ID    
    private Long articleId;

    //收藏用户ID    
    private Long userId;

    //创建时间    
    private Date createTime;

}


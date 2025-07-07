package com.johnfnash.learn.redis.counter.entity;

import java.util.Date;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * 文章阅读记录表(ArticleRead)表实体类
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@TableName("article_read")
public class ArticleRead implements Serializable {
    private static final long serialVersionUID = -77419038574489216L;

    //自增主键    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    //文章ID
    private Long articleId;

    private Long totalReadCount;

    private Date createTime;

    private Date updateTime;

}


package com.johnfnash.learn.redis.counter.entity;

import java.util.Date;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * 文章信息表(Article)表实体类
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@TableName("article")
public class Article implements Serializable {
    private static final long serialVersionUID = 877977810708732054L;

    //文章ID    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    //作者ID    
    private Long userId;

    //文章标题    
    private String title;

    //文章内容    
    private String content;

//    //点赞数
//    private Integer praiseCount;
//
//    //阅读数
//    private Integer readCount;
//
//    //收藏数
//    private Integer collectionCount;

    //状态(1-发布,2-删除)
    private Integer status;

    //创建时间    
    private Date createTime;

    //更新时间    
    private Date updateTime;

}


package com.johnfnash.learn.redis.counter.entity;

import java.util.Date;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * 用户基础信息表(User)表实体类
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = -58474714033204077L;

    //用户ID    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    //用户名    
    private String username;

    //密码(加密存储)    
    private String password;

    //关注数    
    private Integer followCount;

    //粉丝数    
    private Integer fansCount;

    //已发布文章数    
    private Integer articleCount;

    //创建时间    
    private Date createTime;

    //更新时间    
    private Date updateTime;

}


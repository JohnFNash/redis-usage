package com.johnfnash.learn.redis.counter.entity;

import java.util.Date;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * 用户关注关系表(UserFollow)表实体类
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@TableName("user_follow")
public class UserFollow implements Serializable {
    private static final long serialVersionUID = -16726507357066694L;

    //主键ID    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    //关注者ID    
    private Long userId;

    //被关注者ID    
    private Long followedUserId;

    //创建时间    
    private Date createTime;

}


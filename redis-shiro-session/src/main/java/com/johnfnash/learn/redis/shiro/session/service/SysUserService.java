package com.johnfnash.learn.redis.shiro.session.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.johnfnash.learn.redis.shiro.session.entity.SysUserEntity;
import com.johnfnash.learn.redis.shiro.session.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

/**
 * 系统用户业务实现
 */
@Service("sysUserService")
public class SysUserService extends ServiceImpl<SysUserMapper, SysUserEntity> {

    /**
     * 根据用户名查询实体
     * @Param  username 用户名
     * @Return SysUserEntity 用户实体
     */
    public SysUserEntity selectUserByName(String username) {
        QueryWrapper<SysUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserEntity::getUsername, username);
        return this.baseMapper.selectOne(queryWrapper);
    }

}
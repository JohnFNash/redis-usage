package com.johnfnash.learn.redis.shiro.session.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.johnfnash.learn.redis.shiro.session.entity.SysUserRoleEntity;
import com.johnfnash.learn.redis.shiro.session.mapper.SysUserRoleMapper;
import org.springframework.stereotype.Service;

/**
 * 用户与角色业务实现
 */
@Service("sysUserRoleService")
public class SysUserRoleService extends ServiceImpl<SysUserRoleMapper, SysUserRoleEntity> {

}
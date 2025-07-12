package com.johnfnash.learn.redis.shiro.session.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.johnfnash.learn.redis.shiro.session.entity.SysRoleMenuEntity;
import com.johnfnash.learn.redis.shiro.session.mapper.SysRoleMenuMapper;
import org.springframework.stereotype.Service;

/**
 * 角色与权限业务实现
 */
@Service("sysRoleMenuService")
public class SysRoleMenuService extends ServiceImpl<SysRoleMenuMapper, SysRoleMenuEntity> {

}
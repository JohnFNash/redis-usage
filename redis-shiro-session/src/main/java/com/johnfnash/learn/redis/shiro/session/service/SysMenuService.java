package com.johnfnash.learn.redis.shiro.session.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.johnfnash.learn.redis.shiro.session.entity.SysMenuEntity;
import com.johnfnash.learn.redis.shiro.session.mapper.SysMenuMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限业务实现
 */
@Service("sysMenuService")
public class SysMenuService extends ServiceImpl<SysMenuMapper, SysMenuEntity> {


    /**
     * 根据角色查询用户权限
     * @Param  roleId 角色ID
     * @Return List<SysMenuEntity> 权限集合
     */
    public List<SysMenuEntity> selectSysMenuByRoleId(Long roleId) {
        return this.baseMapper.selectSysMenuByRoleId(roleId);
    }

}
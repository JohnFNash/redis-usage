package com.johnfnash.learn.redis.shiro.session.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.johnfnash.learn.redis.shiro.session.entity.SysMenuEntity;

import java.util.List;

/**
 * 权限Mapper
 */
public interface SysMenuMapper extends BaseMapper<SysMenuEntity> {

    /**
     * 根据角色查询用户权限
     * @Param  roleId 角色ID
     * @Return List<SysMenuEntity> 权限集合
     */
    List<SysMenuEntity> selectSysMenuByRoleId(Long roleId);
	
}

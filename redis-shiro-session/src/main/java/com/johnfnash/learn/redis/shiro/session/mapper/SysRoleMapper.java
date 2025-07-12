package com.johnfnash.learn.redis.shiro.session.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.johnfnash.learn.redis.shiro.session.entity.SysRoleEntity;

import java.util.List;

/**
 * 角色Mapper
 */
public interface SysRoleMapper extends BaseMapper<SysRoleEntity> {

    /**
     * 通过用户ID查询角色集合
     * @Param  userId 用户ID
     * @Return List<SysRoleEntity> 角色名集合
     */
    List<SysRoleEntity> selectSysRoleByUserId(Long userId);
	
}

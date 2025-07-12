package com.johnfnash.learn.redis.shiro.session.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.johnfnash.learn.redis.shiro.session.entity.SysRoleEntity;
import com.johnfnash.learn.redis.shiro.session.mapper.SysRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色业务实现
 */
@Service("sysRoleService")
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRoleEntity> {

    /**
     * 通过用户ID查询角色集合
     * @Param  userId 用户ID
     * @Return List<SysRoleEntity> 角色名集合
     */
    public List<SysRoleEntity> selectSysRoleByUserId(Long userId) {
        return this.baseMapper.selectSysRoleByUserId(userId);
    }

}
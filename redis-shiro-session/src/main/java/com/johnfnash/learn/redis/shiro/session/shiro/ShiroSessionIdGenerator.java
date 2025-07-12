package com.johnfnash.learn.redis.shiro.session.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * 自定义SessionId生成器
 */
public class ShiroSessionIdGenerator implements SessionIdGenerator {

    /**
     * 实现SessionId生成
     */
    @Override
    public Serializable generateId(Session session) {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}

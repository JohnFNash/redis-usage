package com.johnfnash.learn.redis.shiro.session;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.johnfnash.learn.redis.shiro.session.mapper"}) //扫描Mapper
public class RedisShiroSessionApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisShiroSessionApplication.class, args);
    }

}

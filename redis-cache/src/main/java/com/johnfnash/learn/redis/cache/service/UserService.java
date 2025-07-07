package com.johnfnash.learn.redis.cache.service;

import com.johnfnash.learn.redis.cache.entity.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "user") //指定cache的名字,这里指定了 caheNames，下面的方法的注解里就可以不用指定 value 属性了
public class UserService {

    private Map<Long, User> userMap = new HashMap<>();

    public UserService() {
        User u1=new User();
        u1.setId(1L);
        u1.setName("1111");
        u1.setPassword("11223434");
        User u2=new User();
        u2.setId(2L);
        u2.setName("1111");
        u2.setPassword("11223434");
        User u3=new User();
        u3.setId(3L);
        u3.setName("1111");
        u3.setPassword("11223434");

        userMap.put(1L,u1);
        userMap.put(2L,u2);
        userMap.put(3L,u3);
    }

    @Cacheable(key = "'list'")
    public List<User> list() {
        System.out.println("querying list.....");
        User[] users = new User[userMap.size()];
        this.userMap.values().toArray(users);
        return Arrays.asList(users);
    }

    @Cacheable(key = "''.concat(#id.toString())")
    public User findUserById(Long id) {
        System.out.println("find user by id...");
        return userMap.get(id);
    }

    // 清空cache
    @CacheEvict(key = "''.concat(#id.toString())")
    public void remove(Long id) {
        System.out.println("remove user...");
        userMap.remove(id);
    }

    @CacheEvict(key = "''.concat(#id.toString())")
    public User upuser(Long id) {
        System.out.println("update user and remove cache...");
        User d = userMap.get(id);
        d.setName("0000000000000000000000000000000000000000");
        return d;
    }

    @CachePut(key = "''.concat(#user.id)")
    public User saveUser(User user) {
        System.out.println("each time add/update user and update cache...");
        userMap.put(user.getId(), user);
        return user;
    }

    public List<User> findTop10() {
        User[] users = new User[userMap.size()];
        this.userMap.values().toArray(users);
        return Arrays.asList(users);
    }

}

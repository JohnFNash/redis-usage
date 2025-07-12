package com.johnfnash.learn.redis.session.controller;

import com.johnfnash.learn.redis.session.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    Map<String, User> userMap = new HashMap<>();

    public UserController() {
        //初始化1个用户，用于模拟登录
        User u1 = new User(1,"user1","user1");
        userMap.put("user1",u1);
    }

    /**
     * 拿当前用户的session
     */
    @GetMapping(value = "/session")
    public String session(HttpSession session) {
        log.info("当前用户的session={}",session.getId());
        return session.getId();
    }

    @GetMapping(value = "/login")
    public String login(String username, String password, HttpSession session) {
        //模拟数据库的查找
        User user = this.userMap.get(username);
        if (user != null) {
            if (!password.equals(user.getPassword())) {
                return "用户名或密码错误！！！";
            } else {
                session.setAttribute(session.getId(), user);
                log.info("登录成功{}",user);
            }
        } else {
            return "用户名或密码错误！！！";
        }
        return "登录成功！！！";
    }

    /**
     * 退出登录
     */
    @GetMapping(value = "/logout")
    public String logout(HttpSession session) {
        log.info("退出登录session={}",session.getId());
        session.removeAttribute(session.getId());
        // 执行注销操作，这将导致Session失效
        session.invalidate();
        return "成功退出！！";
    }

}
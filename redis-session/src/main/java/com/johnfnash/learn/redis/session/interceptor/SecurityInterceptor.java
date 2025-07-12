package com.johnfnash.learn.redis.session.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Configuration
public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        // 验证当前session是否存在，存在返回true true代表能正常处理业务逻辑
        if (session.getAttribute(session.getId()) != null) {
            log.info("session拦截器，session={}，验证通过", session.getId());
            return true;
        }
        // session不存在，返回false，并提示请重新登录。
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write("请登录！！！！！");
        log.info("session拦截器，session={}，验证失败", session.getId());
        return false;
    }

}

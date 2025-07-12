package com.johnfnash.learn.redis.shiro.session.shiro;

import com.alibaba.fastjson.JSON;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Shiro自定义拦截器(需要在 shiroConfig 进行注册)
 * 过滤OPTIONS请求,减少会话生成数量
 * 继承shiro 的form表单过滤器，对 OPTIONS 请求进行过滤。
 * 前后端分离项目中，由于跨域，会导致复杂请求，即会发送preflighted request，这样会导致在GET／POST等请求之前会先发一个OPTIONS请求
 * 但 OPTIONS 请求并不带shiro的'authToken'字段（shiro的SessionId），即OPTIONS请求不能通过shiro验证，会返回未认证的信息。
 */
public class ShiroAuthenticationFilter extends FormAuthenticationFilter {

    /**
     * 直接过滤可以访问的请求类型
     */
    private static final String REQUEST_TYPE = "OPTIONS";

    public ShiroAuthenticationFilter() {
        super();
    }

    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (((HttpServletRequest) request).getMethod().equals(REQUEST_TYPE)) {
            return true;
        }
        return super.isAccessAllowed(request, response, mappedValue);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse res = (HttpServletResponse) response;
        res.setHeader("Content-type", "text/html;charset=UTF-8");
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setContentType("application/json");
        res.setStatus(HttpServletResponse.SC_OK);
        res.setCharacterEncoding("UTF-8");
        PrintWriter writer = res.getWriter();
        Map<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("msg", "未登录");
        writer.write(JSON.toJSONString(map));
        writer.close();
        return false;
    }

}

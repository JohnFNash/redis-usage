package com.johnfnash.learn.redis.session.config;

import com.johnfnash.learn.redis.session.interceptor.SecurityInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SessionCofig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityInterceptor())
                // 排除拦截的2个路径
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/logout")
                // 拦截所有URL路径
                .addPathPatterns("/**");
    }

}

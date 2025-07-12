package com.johnfnash.learn.redis.shiro.session.config;

import com.johnfnash.learn.redis.shiro.session.shiro.ShiroAuthenticationFilter;
import com.johnfnash.learn.redis.shiro.session.shiro.ShiroRealm;
import com.johnfnash.learn.redis.shiro.session.shiro.ShiroSessionIdGenerator;
import com.johnfnash.learn.redis.shiro.session.shiro.ShiroSessionManager;
import com.johnfnash.learn.redis.shiro.session.util.SHA256Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置类
 */
@Configuration
public class ShiroConfig {

    private final String CACHE_KEY = "sr:ca:";
    private final String SESSION_KEY = "sr:ss:";

    //Redis配置
    @Value("${shiro.redis.host}")
    private String host;
    @Value("${spring.redis.password:}")
    private String password;
    @Value("${spring.redis.timeout}")
    private int timeout;

    /**
     * 开启Shiro-aop注解支持
     * 使用代理方式所以需要开启代码支持
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * Shiro基础配置
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactory(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 自定义拦截器
        Map<String, Filter> customFilterMap = new LinkedHashMap<>();
        customFilterMap.put("shiroAuthenticationFilter", new ShiroAuthenticationFilter());
        shiroFilterFactoryBean.setFilters(customFilterMap);
        // 拦截器，配置访问权限 必须是LinkedHashMap，因为它必须保证有序。滤链定义，从上向下顺序执行，一般将 /**放在最为下边
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 配置过滤:不会被拦截的链接
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/userLogin/**", "anon");
        // 最终执行自定义拦截器
        filterChainDefinitionMap.put("/**", "shiroAuthenticationFilter");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 安全管理器
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 自定义Ssession管理
        securityManager.setSessionManager(sessionManager());
        // 自定义Cache实现
        securityManager.setCacheManager(cacheManager());
        // 自定义Realm验证
        securityManager.setRealm(shiroRealm());
        return securityManager;
    }

    /**
     * 身份验证器
     */
    @Bean
    public ShiroRealm shiroRealm() {
        ShiroRealm shiroRealm = new ShiroRealm();
        // 非https的情况下，这样使用比较危险，登录时需要输入密码明文，建议搭配https使用
        shiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());

        //缓存AuthorizationInfo信息的缓存名称，这里不设置时默认使用ShiroRealm全类名+授权缓存名称，
        // 即sr:arc:ShiroRealm.class.getName()+"authorizationCache"
        //shiroRealm.setAuthorizationCacheName("sr:arc:");

        // 开启缓存
        shiroRealm.setCachingEnabled(true);
        // 开启身份验证缓存，即缓存AuthenticationInfo信息
        shiroRealm.setAuthenticationCachingEnabled(true);
        // 设置身份缓存名称前缀
        shiroRealm.setAuthenticationCacheName("sc:atc");
        // 开启授权缓存
        shiroRealm.setAuthorizationCachingEnabled(true);
        // 这是权限缓存名称前缀
        shiroRealm.setAuthorizationCacheName("sc:arc:");

        return shiroRealm;
    }

    /**
     * 凭证匹配器
     * 将密码校验交给Shiro的SimpleAuthenticationInfo进行处理,在这里做匹配配置
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher shaCredentialsMatcher = new HashedCredentialsMatcher();
        // 散列算法:这里使用SHA256算法;
        shaCredentialsMatcher.setHashAlgorithmName(SHA256Util.HASH_ALGORITHM_NAME);
        // 散列的次数，比如散列两次，相当于 md5(md5(""));
        shaCredentialsMatcher.setHashIterations(SHA256Util.HASH_ITERATIONS);
        return shaCredentialsMatcher;
    }

    /**
     * 配置Redis管理器
     */
    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setTimeout(timeout);
        if (StringUtils.isNotEmpty(password)) {
            redisManager.setPassword(password);
        }
        return redisManager;
    }

    /**
     * 配置Cache管理器
     * 用于往Redis存储权限和角色标识
     */
    @Bean
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        redisCacheManager.setKeyPrefix(CACHE_KEY);
        // 配置缓存的话要求放在session里面的实体类必须有个id标识
        redisCacheManager.setPrincipalIdFieldName("userId");
        redisCacheManager.setExpire(timeout);
        return redisCacheManager;
    }

    /**
     * SessionID生成器
     */
    @Bean
    public ShiroSessionIdGenerator sessionIdGenerator(){
        return new ShiroSessionIdGenerator();
    }

    // 这里改为从请求头里取，不从cookie里取
//    @Bean
//    public SimpleCookie cookie(){
//        SimpleCookie cookie = new SimpleCookie("SHAREJSESSIONID"); //  cookie的name,对应的默认是 JSESSIONID
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");        //  path为 / 用于多个系统共享JSESSIONID
//        return cookie;
//    }

    /**
     * 配置RedisSessionDAO
     * 使用的是shiro-redis开源插件
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setSessionIdGenerator(sessionIdGenerator());
        redisSessionDAO.setKeyPrefix(SESSION_KEY);
        redisSessionDAO.setExpire(timeout);
        return redisSessionDAO;
    }

    /**
     * 配置Session管理器
     */
    @Bean
    public SessionManager sessionManager() {
        ShiroSessionManager shiroSessionManager = new ShiroSessionManager();
        shiroSessionManager.setSessionDAO(redisSessionDAO());
        shiroSessionManager.setCacheManager(cacheManager());
        // 设置全局session过期时间
        shiroSessionManager.setGlobalSessionTimeout(timeout*1000);
        // 取消URL后面的JSESSIONID
        shiroSessionManager.setSessionIdUrlRewritingEnabled(false);
        return shiroSessionManager;
    }

}
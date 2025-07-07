package com.johnfnash.learn.redis.cache.config;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
@EnableCaching // Enables Spring's annotation-driven cache management capability
public class RedisConfig extends CachingConfigurerSupport {

    @Value("${spring.redis.host}")
    private String hostName;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${redis.time-to-live.seconds}")
    private int entryTTL;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(hostName, port);
        return new LettuceConnectionFactory(config);
    }

    /**
     * 自定义缓存键生成器，考虑方法名和参数
     * @return
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            // 当没有指定缓存的 key时来根据类名、方法名和方法参数来生成key
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append('.').append(method.getName());
                if(params.length > 0) {
                    sb.append('[');
                    for (Object obj : params) {
                        sb.append(obj.toString());
                    }
                    sb.append(']');
                }
                System.out.println("keyGenerator=" + sb.toString());
                return sb.toString();
            }
        };
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return new RedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory),
                this.getRedisCacheConfigurationWithTtl(Duration.ofSeconds(entryTTL)),  // 默认策略，未配置的 key 会使用这个
                this.getRedisCacheConfigurationMap()               // 指定 key 策略
        );

        // 创建支持动态TTL的RedisCacheManager
        // 创建自定义的RedisCacheWriter
        //RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        // 默认缓存配置
        //RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
        //        .entryTtl(Duration.ofHours(1)); // 默认过期时间1小时
        //return new DynamicTtlRedisCacheManager(cacheWriter, defaultConfig);
    }

    private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();

        // 单独设置某些cache的超时时间

        // 用户缓存：过期时间30分钟
        configMap.put("user", getRedisCacheConfigurationWithTtl(Duration.ofMinutes(30)));

        // 产品缓存：过期时间2小时
        configMap.put("product", getRedisCacheConfigurationWithTtl(Duration.ofHours(2)));

        // 热点数据缓存：过期时间5分钟
        configMap.put("hotData", getRedisCacheConfigurationWithTtl(Duration.ofMinutes(5)));

        return configMap;
    }

    /**
     * 获取默认缓存配置
     * @param duration
     * @return
     */
    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Duration duration) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        RedisSerializer<Object> valueSerializer = getValueSerializer();
        redisCacheConfiguration = redisCacheConfiguration
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .entryTtl(duration);
        return redisCacheConfiguration;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        RedisSerializer<Object> valueSerializer = getValueSerializer();
        template.setValueSerializer(valueSerializer);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    private RedisSerializer<Object> getValueSerializer() {
        // 1. 默认值序列化器 JdkSerializationRedisSerializer
        // RedisCacheConfiguration默认就是使用StringRedisSerializer序列化key，JdkSerializationRedisSerializer序列化value,所以以下注释代码为默认实现
        //ClassLoader loader = this.getClass().getClassLoader();
        //JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(loader);
        //return jdkSerializer;

        // 2. JSON序列化器
        //Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        //ObjectMapper om = new ObjectMapper();
        //om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        //jackson2JsonRedisSerializer.setObjectMapper(om);
        //return jackson2JsonRedisSerializer;

        // 3. Kryo序列化器
        return new KryoRedisSerializer<>();
    }

    // 自定义RedisCacheManager，支持动态TTL
    static class DynamicTtlRedisCacheManager  extends RedisCacheManager {

        public DynamicTtlRedisCacheManager (RedisCacheWriter cacheWriter,
                                            RedisCacheConfiguration defaultCacheConfiguration) {
            super(cacheWriter, defaultCacheConfiguration);
        }

        @Override
        protected RedisCache createRedisCache(String name, RedisCacheConfiguration config) {
            // 根据缓存名称动态设置TTL
            // 根据缓存名称动态设置TTL
            if (name.startsWith("userActivity")) {
                config = config.entryTtl(Duration.ofMinutes(15));
            } else if (name.startsWith("product")) {
                config = config.entryTtl(Duration.ofHours(4));
            } else if (name.startsWith("config")) {
                config = config.entryTtl(Duration.ofDays(1));
            }
            return super.createRedisCache(name, config);
        }
    }

}
package com.johnfnash.learn.redis.shoppingcart.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.MySqlDialect;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.johnfnash.learn.redis.shoppingcart.**.mapper*")
public class MybatisPlusConfig {

    @Value("${spring.datasource.url:}")
    private String dataSourceUrl;

    /**
     * 分页插件
     *
     * @param maxPageSize 最大分页数
     */
    @Bean
    public MybatisPlusInterceptor InnerInterceptor(@Value("${app-config.mybatisplus.maxPageSize:1000}") long maxPageSize) {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor page = new PaginationInnerInterceptor();
        if (dataSourceUrl.contains("mysql")) {
            IDialect dialect = new MySqlDialect();
            page.setDialect(dialect);
        }
        page.setMaxLimit(maxPageSize);
        mybatisPlusInterceptor.addInnerInterceptor(page);
        return mybatisPlusInterceptor;
    }
}

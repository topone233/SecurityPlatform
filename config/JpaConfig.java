package org.example.securityplatform.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JPA配置类
 */
@Configuration
public class JpaConfig {

    /**
     * 配置Jackson序列化，处理LocalDateTime等时间类型
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.simpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
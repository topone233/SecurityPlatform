package org.example.securityplatform.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 组件类型常量
 */
@Getter
@AllArgsConstructor
public class ComponentType {
    // ORM
    public static final String MYBATIS = "ORM";
    public static final String HIBERNATE = "ORM";
    public static final String JPA = "ORM";

    // 缓存
    public static final String REDIS = "CACHE";
    public static final String EHCACHE = "CACHE";

    // 消息队列
    public static final String KAFKA = "MQ";
    public static final String RABBITMQ = "MQ";

    // 数据库
    public static final String MONGODB = "DATABASE";
    public static final String ELASTICSEARCH = "SEARCH";

    // Web框架
    public static final String SPRING_MVC = "WEB";
    public static final String JAX_RS = "WEB";
}
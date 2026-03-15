package org.example.securityplatform.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * HTTP方法枚举
 */
@Getter
@AllArgsConstructor
public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS");

    private final String value;
}
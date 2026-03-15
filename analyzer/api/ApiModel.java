package org.example.securityplatform.analyzer.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API分析模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiModel {
    private String url;
    private String httpMethod;
    private String controllerClass;
    private String methodName;
    private String parameters;
    private String framework;
}
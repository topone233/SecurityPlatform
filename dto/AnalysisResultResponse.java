package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分析结果响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResultResponse {
    private Long artifactId;
    private String status;
    private List<ApiSummary> apis;
    private List<ComponentSummary> components;
    private List<DangerSummary> dangers;
    private List<ConfigSummary> configs;
    private List<UrlSummary> urls;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiSummary {
        private String url;
        private String httpMethod;
        private String controllerClass;
        private String methodName;
        private String framework;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentSummary {
        private String componentName;
        private String componentType;
        private String detectionSource;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DangerSummary {
        private String functionName;
        private String functionType;
        private String className;
        private Integer lineNumber;
        private String riskLevel;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfigSummary {
        private String configKey;
        private String configValue;
        private String category;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UrlSummary {
        private String url;
        private String protocol;
        private String usageContext;
    }
}
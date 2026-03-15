package org.example.securityplatform.analyzer.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组件分析模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentModel {
    private String componentName;
    private String componentType;
    private String version;
    private String detectionSource;
    private String details;
}
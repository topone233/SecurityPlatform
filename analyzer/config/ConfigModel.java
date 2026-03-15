package org.example.securityplatform.analyzer.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配置分析模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigModel {
    private String configKey;
    private String configValue;
    private String configSource;
    private String filePath;
    private String category;
}
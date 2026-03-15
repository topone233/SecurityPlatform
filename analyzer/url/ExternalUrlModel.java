package org.example.securityplatform.analyzer.url;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 外部URL分析模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalUrlModel {
    private String url;
    private String protocol;
    private String usageContext;
    private String filePath;
}
package org.example.securityplatform.analyzer.danger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 危险函数分析模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DangerFunctionModel {
    private String functionName;
    private String functionType;
    private String className;
    private Integer lineNumber;
    private String filePath;
    private String methodName;
    private String riskLevel;
}
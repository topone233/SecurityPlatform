package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试用例请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseRequest {
    private String caseName;
    private String vulnerabilityType;
    private String riskLevel;
    private String applicableComponents;
    private String applicableDangerFunctions;
    private String detectionMethod;
    private String testSteps;
    private String payloadTemplate;
    private String expectedResult;
    private String references;
}
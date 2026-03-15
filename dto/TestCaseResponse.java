package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 测试用例响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseResponse {
    private Long id;
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
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
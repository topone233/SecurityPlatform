package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试执行请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestExecutionRequest {
    private Long artifactId;
    private Long checklistId;
    private Long testCaseId;
    private String executionName;
    private String vulnerabilityType;
    private String executionType;
    private String executor;
    private String environmentInfo;
    private String testData;
    private String expectedResult;
    private String notes;
}

package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 测试执行记录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestExecutionResponse {
    private Long id;
    private Long artifactId;
    private Long checklistId;
    private Long testCaseId;
    private String executionName;
    private String vulnerabilityType;
    private String executionStatus;
    private String executionType;
    private String executor;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMs;
    private String testResult;
    private String severity;
    private Boolean reproducible;
    private String environmentInfo;
    private String testData;
    private String actualResult;
    private String expectedResult;
    private String evidenceFiles;
    private String logOutput;
    private String errorMessage;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

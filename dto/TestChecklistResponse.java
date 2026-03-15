package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 测试清单响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestChecklistResponse {
    private Long id;
    private Long artifactId;
    private Long testCaseId;
    private String caseName;
    private String vulnerabilityType;
    private String riskLevel;
    private String matchedComponent;
    private String matchedDangerFunction;
    private String matchedConfigKey;
    private String status;
    private String exclusionReason;
    private Long exclusionRuleId;
    private String testResult;
    private String testNotes;
    private String testedBy;
    private LocalDateTime testedAt;
    private LocalDateTime createdAt;
}
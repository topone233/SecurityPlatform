package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 风险排除响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskExclusionResponse {
    private Long id;
    private Long artifactId;
    private Long dangerFunctionId;
    private Long ruleId;
    private String functionName;
    private String functionType;
    private String className;
    private String filePath;
    private Integer lineNumber;
    private String originalRiskLevel;
    private String exclusionReason;
    private String confidenceLevel;
    private String status;
    private String reviewer;
    private String reviewNotes;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
}
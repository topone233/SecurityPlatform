package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 分析进度响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisProgressResponse {
    private Long id;
    private Long artifactId;
    private String status;
    private String currentStep;
    private Integer totalSteps;
    private Integer completedSteps;
    private Integer progressPercentage;
    private String message;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
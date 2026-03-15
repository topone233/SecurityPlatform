package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 策略匹配结果响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyMatchResponse {
    private Long artifactId;
    private Integer totalTestCases;
    private Integer todoCount;
    private Integer passCount;
    private Integer naCount;
    private Map<String, Integer> vulnerabilityTypeCounts;
    private Map<String, Integer> riskLevelCounts;
    private List<TestChecklistResponse> testChecklist;
    private Integer exclusionCount;
    private Integer pendingReviewCount;
}
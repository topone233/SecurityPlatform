package org.example.securityplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 测试报告响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestReportResponse {
    private Long id;
    private Long artifactId;
    private String reportName;
    private String reportType;
    private String reportStatus;
    private String generatedBy;
    private LocalDateTime generatedAt;

    // 统计信息
    private Integer totalTestCases;
    private Integer executedCount;
    private Integer passCount;
    private Integer failCount;
    private Integer skipCount;
    private Integer errorCount;

    // 风险统计
    private Integer criticalCount;
    private Integer highCount;
    private Integer mediumCount;
    private Integer lowCount;

    // 执行时间
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long totalDurationMs;

    // 报告内容
    private String summary;
    private Map<String, Object> vulnerabilitySummary;
    private Map<String, Object> componentSummary;
    private String recommendations;

    // 文件信息
    private String reportFilePath;
    private String reportFormat;
    private Long fileSizeBytes;

    private LocalDateTime createdAt;
}

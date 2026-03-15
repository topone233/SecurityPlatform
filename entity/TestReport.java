package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试报告实体
 */
@Data
@Entity
@Table(name = "test_report", indexes = {
    @Index(name = "idx_report_artifact_id", columnList = "artifact_id"),
    @Index(name = "idx_report_status", columnList = "report_status"),
    @Index(name = "idx_report_type", columnList = "report_type")
})
public class TestReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artifact_id", nullable = false)
    private Long artifactId;

    @Column(name = "report_name", nullable = false, length = 255)
    private String reportName;

    @Column(name = "report_type", nullable = false, length = 20)
    private String reportType = "FULL";

    @Column(name = "report_status", nullable = false, length = 20)
    private String reportStatus = "GENERATING";

    @Column(name = "generated_by", length = 100)
    private String generatedBy;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    // 统计信息
    @Column(name = "total_test_cases")
    private Integer totalTestCases = 0;

    @Column(name = "executed_count")
    private Integer executedCount = 0;

    @Column(name = "pass_count")
    private Integer passCount = 0;

    @Column(name = "fail_count")
    private Integer failCount = 0;

    @Column(name = "skip_count")
    private Integer skipCount = 0;

    @Column(name = "error_count")
    private Integer errorCount = 0;

    // 风险统计
    @Column(name = "critical_count")
    private Integer criticalCount = 0;

    @Column(name = "high_count")
    private Integer highCount = 0;

    @Column(name = "medium_count")
    private Integer mediumCount = 0;

    @Column(name = "low_count")
    private Integer lowCount = 0;

    // 执行时间
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "total_duration_ms")
    private Long totalDurationMs;

    // 报告内容
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "vulnerability_summary", columnDefinition = "JSONB")
    private String vulnerabilitySummary;

    @Column(name = "component_summary", columnDefinition = "JSONB")
    private String componentSummary;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    // 文件信息
    @Column(name = "report_file_path", length = 500)
    private String reportFilePath;

    @Column(name = "report_format", length = 20)
    private String reportFormat = "JSON";

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试执行记录实体
 */
@Data
@Entity
@Table(name = "test_execution", indexes = {
    @Index(name = "idx_execution_artifact_id", columnList = "artifact_id"),
    @Index(name = "idx_execution_checklist_id", columnList = "checklist_id"),
    @Index(name = "idx_execution_status", columnList = "execution_status"),
    @Index(name = "idx_execution_result", columnList = "test_result"),
    @Index(name = "idx_execution_vulnerability_type", columnList = "vulnerability_type")
})
public class TestExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artifact_id", nullable = false)
    private Long artifactId;

    @Column(name = "checklist_id")
    private Long checklistId;

    @Column(name = "test_case_id")
    private Long testCaseId;

    @Column(name = "execution_name", nullable = false, length = 255)
    private String executionName;

    @Column(name = "vulnerability_type", length = 100)
    private String vulnerabilityType;

    @Column(name = "execution_status", nullable = false, length = 20)
    private String executionStatus = "PENDING";

    @Column(name = "execution_type", nullable = false, length = 20)
    private String executionType = "MANUAL";

    @Column(name = "executor", length = 100)
    private String executor;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "test_result", length = 20)
    private String testResult;

    @Column(name = "severity", length = 20)
    private String severity;

    @Column(name = "reproducible")
    private Boolean reproducible;

    @Column(name = "environment_info", columnDefinition = "TEXT")
    private String environmentInfo;

    @Column(name = "test_data", columnDefinition = "TEXT")
    private String testData;

    @Column(name = "actual_result", columnDefinition = "TEXT")
    private String actualResult;

    @Column(name = "expected_result", columnDefinition = "TEXT")
    private String expectedResult;

    @Column(name = "evidence_files", columnDefinition = "JSONB")
    private String evidenceFiles;

    @Column(name = "log_output", columnDefinition = "TEXT")
    private String logOutput;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

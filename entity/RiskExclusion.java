package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 风险排除记录实体
 * 记录具体的风险排除实例
 */
@Data
@Entity
@Table(name = "risk_exclusion", indexes = {
    @Index(name = "idx_exclusion_artifact_id", columnList = "artifact_id"),
    @Index(name = "idx_exclusion_danger_function_id", columnList = "danger_function_id"),
    @Index(name = "idx_exclusion_status", columnList = "status")
})
public class RiskExclusion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artifact_id", nullable = false)
    private Long artifactId;

    @Column(name = "danger_function_id")
    private Long dangerFunctionId;

    @Column(name = "rule_id")
    private Long ruleId;

    @Column(name = "function_name", length = 255)
    private String functionName;

    @Column(name = "function_type", length = 100)
    private String functionType;

    @Column(name = "class_name", length = 255)
    private String className;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "line_number")
    private Integer lineNumber;

    @Column(name = "original_risk_level", length = 20)
    private String originalRiskLevel;

    @Column(name = "exclusion_reason", columnDefinition = "TEXT")
    private String exclusionReason;

    @Column(name = "confidence_level", length = 20)
    private String confidenceLevel;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "reviewer", length = 100)
    private String reviewer;

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
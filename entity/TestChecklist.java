package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试清单实体
 * 为每个制品生成的测试任务清单
 */
@Data
@Entity
@Table(name = "test_checklist", indexes = {
    @Index(name = "idx_checklist_artifact_id", columnList = "artifact_id"),
    @Index(name = "idx_checklist_status", columnList = "status"),
    @Index(name = "idx_checklist_vulnerability_type", columnList = "vulnerability_type")
})
public class TestChecklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artifact_id", nullable = false)
    private Long artifactId;

    @Column(name = "test_case_id")
    private Long testCaseId;

    @Column(name = "case_name", nullable = false, length = 255)
    private String caseName;

    @Column(name = "vulnerability_type", nullable = false, length = 100)
    private String vulnerabilityType;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "matched_component", length = 255)
    private String matchedComponent;

    @Column(name = "matched_danger_function", length = 255)
    private String matchedDangerFunction;

    @Column(name = "matched_config_key", length = 255)
    private String matchedConfigKey;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "TODO";

    @Column(name = "exclusion_reason", columnDefinition = "TEXT")
    private String exclusionReason;

    @Column(name = "exclusion_rule_id")
    private Long exclusionRuleId;

    @Column(name = "test_result", columnDefinition = "TEXT")
    private String testResult;

    @Column(name = "test_notes", columnDefinition = "TEXT")
    private String testNotes;

    @Column(name = "tested_by", length = 100)
    private String testedBy;

    @Column(name = "tested_at")
    private LocalDateTime testedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
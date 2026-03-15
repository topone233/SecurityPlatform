package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试用例库实体
 * 存储预定义的测试用例模板
 */
@Data
@Entity
@Table(name = "test_case")
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_name", nullable = false, length = 255)
    private String caseName;

    @Column(name = "vulnerability_type", nullable = false, length = 100)
    private String vulnerabilityType;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "applicable_components", columnDefinition = "JSONB")
    private String applicableComponents;

    @Column(name = "applicable_danger_functions", columnDefinition = "JSONB")
    private String applicableDangerFunctions;

    @Column(name = "detection_method", length = 50)
    private String detectionMethod;

    @Column(name = "test_steps", columnDefinition = "TEXT")
    private String testSteps;

    @Column(name = "payload_template", columnDefinition = "TEXT")
    private String payloadTemplate;

    @Column(name = "expected_result", columnDefinition = "TEXT")
    private String expectedResult;

    @Column(name = "references", columnDefinition = "TEXT")
    private String references;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

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
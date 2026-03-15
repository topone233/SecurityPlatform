package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 风险排除规则实体
 * 存储风险自动排除规则
 */
@Data
@Entity
@Table(name = "risk_exclusion_rule")
public class RiskExclusionRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_name", nullable = false, length = 255)
    private String ruleName;

    @Column(name = "rule_type", nullable = false, length = 50)
    private String ruleType;

    @Column(name = "applicable_components", columnDefinition = "JSONB")
    private String applicableComponents;

    @Column(name = "applicable_function_types", columnDefinition = "JSONB")
    private String applicableFunctionTypes;

    @Column(name = "exclusion_condition", columnDefinition = "TEXT")
    private String exclusionCondition;

    @Column(name = "exclusion_reason", columnDefinition = "TEXT")
    private String exclusionReason;

    @Column(name = "confidence_level", length = 20)
    private String confidenceLevel;

    @Column(name = "requires_manual_review", nullable = false)
    private Boolean requiresManualReview = false;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "priority")
    private Integer priority = 100;

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
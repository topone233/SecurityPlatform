package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 配置检查清单实体
 * 存储配置安全检查规则
 */
@Data
@Entity
@Table(name = "config_check_item")
public class ConfigCheckItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", nullable = false, length = 255)
    private String itemName;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "config_key_pattern", nullable = false, length = 255)
    private String configKeyPattern;

    @Column(name = "check_rule", columnDefinition = "TEXT")
    private String checkRule;

    @Column(name = "expected_value", length = 500)
    private String expectedValue;

    @Column(name = "unsafe_values", columnDefinition = "JSONB")
    private String unsafeValues;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "remediation", columnDefinition = "TEXT")
    private String remediation;

    @Column(name = "standard_reference", length = 255)
    private String standardReference;

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
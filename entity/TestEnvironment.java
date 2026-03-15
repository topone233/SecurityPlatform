package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试环境实体
 */
@Data
@Entity
@Table(name = "test_environment", indexes = {
    @Index(name = "idx_environment_type", columnList = "env_type"),
    @Index(name = "idx_environment_active", columnList = "is_active")
})
public class TestEnvironment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "env_name", nullable = false, length = 100)
    private String envName;

    @Column(name = "env_type", nullable = false, length = 50)
    private String envType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "config_data", columnDefinition = "JSONB")
    private String configData;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

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

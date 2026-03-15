package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 组件信息实体
 */
@Data
@Entity
@Table(name = "component_info")
public class ComponentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artifact_id", nullable = false)
    private Long artifactId;

    @Column(name = "component_name", nullable = false, length = 255)
    private String componentName;

    @Column(name = "component_type", nullable = false, length = 50)
    private String componentType;

    @Column(name = "version", length = 100)
    private String version;

    @Column(name = "detection_source", nullable = false, length = 50)
    private String detectionSource;

    @Column(name = "details", columnDefinition = "JSONB")
    private String details;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
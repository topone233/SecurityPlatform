package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API信息实体
 */
@Data
@Entity
@Table(name = "api_info")
public class ApiInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artifact_id", nullable = false)
    private Long artifactId;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;

    @Column(name = "controller_class", length = 255)
    private String controllerClass;

    @Column(name = "method_name", length = 255)
    private String methodName;

    @Column(name = "parameters", columnDefinition = "JSONB")
    private String parameters;

    @Column(name = "framework", nullable = false, length = 50)
    private String framework;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
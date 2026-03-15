package org.example.securityplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 危险函数实体
 */
@Data
@Entity
@Table(name = "danger_function")
public class DangerFunction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artifact_id", nullable = false)
    private Long artifactId;

    @Column(name = "function_name", nullable = false, length = 255)
    private String functionName;

    @Column(name = "function_type", nullable = false, length = 100)
    private String functionType;

    @Column(name = "class_name", length = 255)
    private String className;

    @Column(name = "line_number")
    private Integer lineNumber;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "method_name", length = 255)
    private String methodName;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}